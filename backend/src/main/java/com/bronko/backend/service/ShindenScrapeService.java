package com.bronko.backend.service;

import com.bronko.backend.model.Player;
import com.bronko.backend.model.RelatedSeries;
import com.bronko.backend.model.Series;
import com.bronko.backend.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShindenScrapeService {
    final String baseUrl = "https://shinden.pl";
    final String apiUrl = "https://api4.shinden.pl/xhr";

    public Series getSeries(int id) throws IOException {
        Document document;
        try {
            document = Jsoup.connect(baseUrl + "/series/" + id).get();
        } catch (HttpStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage(), e);
        }

        Series series = new Series();

        series.setSeriesId(id);

        Element leftMenu = document.selectFirst("aside.info-aside.aside-title");

        if (leftMenu != null) {
            Element coverImg = leftMenu.selectFirst("img.info-aside-img[src]");
            if (coverImg != null) {
                series.setImgUrl(coverImg.attr("src"));
            }

            Element ratingData = leftMenu.selectFirst("div.bd");
            if (ratingData != null) {
                Element rating = ratingData.selectFirst("span.info-aside-rating-user");

                if (rating != null) {
                    series.setRating(Double.parseDouble(rating.text().replaceAll(",", ".")));
                }

                Element votes = ratingData.selectFirst("span.h6");

                if (votes != null) {
                    series.setVotes(Utils.parseFirstDigitsInString(votes.text()));
                }
            }

            Element seriesInfoContainer = leftMenu.selectFirst("dl.info-aside-list");
            if (seriesInfoContainer != null) {
                Elements seriesInfo = seriesInfoContainer.select("dd");

                List<String> seriesInfoText = seriesInfo.eachText();
                series.setDistributionType(seriesInfoText.get(0));
                series.setStatus(seriesInfoText.get(1));
                series.setEmissionDate(seriesInfoText.get(2));

                try {
                    series.setEpisodeCount(Integer.parseInt(seriesInfoText.get(3)));
                } catch (NumberFormatException e) {
                    series.setEpisodeCount(Utils.parseIntDefaultZero(seriesInfoText.get(4)));
                }
            }
        }


        Element title = document.selectFirst("span.title");
        if (title != null) {
            series.setTitle(title.text());
        }

        Element description = document.selectFirst("div#description > p");
        if (description != null) {
            series.setDescription(description.text());
        }

        Elements relatedSeriesElements = document.select("li.relation_t2t > figure");

        for (Element relatedSeriesElement : relatedSeriesElements) {
            series.addRelatedSeries(parseRelatedSeriesInfo(relatedSeriesElement));
        }

        return series;
    }

    public RelatedSeries parseRelatedSeriesInfo(Element elem) {
        RelatedSeries relatedSeries = new RelatedSeries();

        Element link = elem.selectFirst("a[href]");
        if (link != null) {
            String url = link.attr("href");
            String[] splitUrl = url.split("/");
            relatedSeries.setSeriesId(Utils.parseFirstDigitsInString(splitUrl[splitUrl.length - 1]));

            relatedSeries.setTitle(link.attr("title"));
        }

        Element img = elem.selectFirst("img[src]");
        if (img != null) {
            relatedSeries.setImgUrl(img.attr("src"));
        }

        Elements figcaption = elem.select("figcaption.figure-type");

        if (figcaption.size() > 0) {
            relatedSeries.setMediaType(figcaption.get(0).text());
        }
        if (figcaption.size() > 1) {
            relatedSeries.setRelationType(figcaption.get(1).text());
        }
        return relatedSeries;
    }

    public Player getPlayer(int id) throws IOException, InterruptedException {
        String auth = "X2d1ZXN0XzowLDUsMjEwMDAwMDAsMjU1LDQxNzQyOTM2NDQ%3D";
        String preloadIframeURL = apiUrl + "/" + id + "/player_load?auth=" + auth;
        String loadIframeURL = apiUrl + "/" + id + "/player_show?auth=" + auth + "&width=765&height=-1";

        Connection.Response res = Jsoup.connect(preloadIframeURL).execute();

        Thread.sleep(5000);


        String cookie = res.cookie("api.shinden");
        if (cookie == null) return null;

        Document document = Jsoup.connect(loadIframeURL).cookie("api.shinden", cookie).get();
        Element iframe = document.selectFirst("iframe");

        Player player = new Player();
        if (iframe != null) {
            String iframeStr = iframe.toString();
            player.setIframe(iframeStr);
            player.setPlayerId(id);
        }
        return player;
    }
}
