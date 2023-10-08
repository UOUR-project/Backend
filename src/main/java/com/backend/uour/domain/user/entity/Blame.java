package com.backend.uour.domain.user.entity;


import com.backend.uour.domain.community.entity.Board;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Blame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY) // 신고한 사람이 사라져도 신고 기록은 남음
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE) // 신고당한 사람이 사라지면 그 신고 기록도 사라짐
    private User pointed;

    private BLAME_CATEGORY blameCategory;
}
