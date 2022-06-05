package com.bronko.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "related_series")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatedSeries {
    @Id
    @Column(name = "r_series_id")
    private int seriesId;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_to")
    private Series relatedTo;

    private String title = "";

    private String mediaType = "";

    private String relationType = "";

    private String imgUrl = "";
}
