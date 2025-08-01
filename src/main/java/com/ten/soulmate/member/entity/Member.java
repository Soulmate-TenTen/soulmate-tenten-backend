package com.ten.soulmate.member.entity;

import java.util.List;

import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.chatting.entity.ChattingList;
import com.ten.soulmate.road.entity.Road;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(length = 50)
    private String pw;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String profileImg;

    @Column(length = 50)
    private String soulmateName;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAttribute> attributes;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Chatting> chattings;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Road> roads;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChattingList> chattingLists;
}

