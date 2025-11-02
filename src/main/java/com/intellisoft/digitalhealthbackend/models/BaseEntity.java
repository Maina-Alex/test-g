package com.intellisoft.digitalhealthbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Instant createdOn;
    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastModifiedDate;
    @Column(name = "soft-Delete")
    private Boolean softDelete;

    @PrePersist
    public void prePersist() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        ZoneId zoneId = ZoneId.of("Africa/Nairobi");
        ZonedDateTime kenya = zonedDateTime.withZoneSameInstant(zoneId);
        this.createdOn = kenya.toInstant();
        this.softDelete = Boolean.FALSE;
    }
}
