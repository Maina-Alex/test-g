package com.intellisoft.digitalhealthbackend.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String createdBy;
    @LastModifiedBy private String lastModifiedBy;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Instant createdOn;

    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    @Column(name = "soft_delete", nullable = false)
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
