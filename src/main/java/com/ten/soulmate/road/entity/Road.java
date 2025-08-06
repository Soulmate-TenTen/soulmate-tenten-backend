package com.ten.soulmate.road.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "road")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Road {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chatId", nullable = false)
    private Chatting chatting;

    @ManyToOne(optional = false)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @CreationTimestamp
    private LocalDateTime createAt;

    @UpdateTimestamp
    private LocalDateTime updateAt;
   
    @Column(length = 200, name="title")
    private String title;
    
    @Column(length = 500, name="summary")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String answerA;

    @Column(columnDefinition = "TEXT")
    private String answerB;

    @Column(length = 50, name="result")
    private String result;
    
    @Column(name="roadStatus")
    private Integer roadStatus;
    
    @Column(columnDefinition = "TEXT")
    private String review;
    
    @Column(columnDefinition = "TEXT")
    private String thinkinContent;
    
    @Column(columnDefinition = "TEXT")
    private String conclusion;
       
    @Column(length = 100, name="titleA")
    private String titleA;
    
    @Column(length = 100, name="titleB")
    private String titleB;
    
    
    @PrePersist
    public void setDefaultRoadStatus() {
        if (this.roadStatus == null) {
            this.roadStatus = 0;
        }
    }
}

