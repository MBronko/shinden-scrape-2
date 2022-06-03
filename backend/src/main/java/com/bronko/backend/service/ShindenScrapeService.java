package com.bronko.backend.service;

import com.bronko.backend.model.*;
import com.bronko.backend.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public PlayerIframe getPlayerIframe(int playerId) throws IOException, InterruptedException {
        String auth = "X2d1ZXN0XzowLDUsMjEwMDAwMDAsMjU1LDQxNzQyOTM2NDQ%3D";
        String preloadIframeURL = apiUrl + "/" + playerId + "/player_load?auth=" + auth;
        String loadIframeURL = apiUrl + "/" + playerId + "/player_show?auth=" + auth + "&width=765&height=-1";

        Connection.Response res;
        try {
            res = Jsoup.connect(preloadIframeURL).execute();
        } catch (HttpStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage(), e);
        }

        Thread.sleep(5000);


        String cookie = res.cookie("api.shinden");
        if (cookie == null) return null;

        Document document;
        try {
            document = Jsoup.connect(loadIframeURL).cookie("api.shinden", cookie).get();
        } catch (HttpStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage(), e);
        }

        Element iframe = document.selectFirst("iframe");

        PlayerIframe playerIframe = new PlayerIframe();
        if (iframe != null) {
            String iframeStr = iframe.toString();
            playerIframe.setIframe(iframeStr);
        }
        return playerIframe;
    }

    public List<PlayerInfo> getPlayersInfo(int seriesId, int episodeId) throws IOException, JSONException {
        Document document;
        try {
            document = Jsoup.connect(baseUrl + "/episode/" + seriesId + "/view/" + episodeId).header("Accept-Language", "pl").get();
        } catch (HttpStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage(), e);
        }

        Elements playerTable = document.select("div.table-responsive > table > tbody > tr");
        List<PlayerInfo> playerInfoList = new ArrayList<>();

        for (Element tableRow : playerTable) {
            PlayerInfo playerInfo = new PlayerInfo();

            Elements columns = tableRow.select("td");

            if (columns.size() < 6) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Issue when parsing table row: not enough columns");
            }

            Element faviconElem = columns.get(1).selectFirst("span.fav-ico");

            if (faviconElem != null) {
                String style = faviconElem.attr("style");
                String faviconUrl = style.split("\\(")[1].substring(0);
                playerInfo.setSubsFavicon(faviconUrl.substring(0, faviconUrl.length() - 1));

                String authors = faviconElem.attr("title");
                playerInfo.setSubsAuthors(authors);
            }

            Element dataElem = columns.get(5).selectFirst("a");
            if (dataElem != null) {
                String data = dataElem.attr("data-episode");

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(data);
                } catch (JSONException e) {
                    throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Exception when parsing json: " + data);
                }

                // todo: possibility to throw exceptions: not urgent

                playerInfo.setId(Integer.parseInt((String) jsonObject.get("online_id")));
                playerInfo.setService((String) jsonObject.get("player"));
                playerInfo.setLangAudio((String) jsonObject.get("lang_audio"));
                playerInfo.setLangSub((String) jsonObject.get("lang_subs"));
                playerInfo.setResolution((String) jsonObject.get("max_res"));
                playerInfo.setSource((String) jsonObject.get("source"));

                String dateToParse = (String) jsonObject.get("added");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate;
                try {
                    parsedDate = dateFormat.parse(dateToParse);
                } catch (ParseException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception while parsing timestamp: " + dateToParse);
                }
                Timestamp timestamp = new Timestamp(parsedDate.getTime());
                playerInfo.setAdded(timestamp);
            }

            playerInfoList.add(playerInfo);
        }

        return playerInfoList;
    }

    public List<Episode> getEpisodes(int seriesId) {
        return null;
    }
}
