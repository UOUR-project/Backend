package com.backend.uour.domain.user.entity;

import com.backend.uour.domain.photo.entity.Id_Photo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne
    @JoinColumn(name = "Id_Photo")
    private Id_Photo Id_photo;

    @Builder
    public StudentId(User user){
        this.user = user;
    }

    public void setId_Photo(Id_Photo Id_photo){
        this.Id_photo = Id_photo;
    }
}
