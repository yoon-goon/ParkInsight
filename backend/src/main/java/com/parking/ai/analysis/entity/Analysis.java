package com.parking.ai.analysis.entity;

import com.parking.ai.auth.entity.User;
import com.parking.ai.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String frontUrl;
    private String rearUrl;
    private String leftUrl;
    private String rightUrl;

    private Integer parkingScore;
    private Integer washScore;
    private String doorDentRisk;

    @Column(columnDefinition = "TEXT")
    private String weatherSnapshot;

    @Column(columnDefinition = "TEXT")
    private String reportText;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
