package com.ten.soulmate.member.entity;

import java.util.List;
import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.chatting.entity.ChattingList;
import com.ten.soulmate.global.type.MemberType;
import com.ten.soulmate.global.type.SoulMateType;
import com.ten.soulmate.road.entity.Review;
import com.ten.soulmate.road.entity.Road;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, length = 50, name="email")
	    private String email;

	    @Column(length = 50, name="pw")
	    private String pw;
	    
	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false, length = 10, name = "role")
	    private MemberType role;

	    @Column(nullable = false, length = 50, name="name")
	    private String name;

	    @Column(length = 200, name="profileImg")
	    private String profileImg;

	    @Column(length = 50, name="soulmateName")
	    private String soulmateName;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false, length = 10,  name = "soulmateType")
	    private SoulMateType soulmateType;

	    // 연관관계 매핑 - cascade 포함 (체인 삭제 대응)
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Chatting> chattings;

	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<ChattingList> chattingLists;

	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Road> roads;

	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Review> reviews;

	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<MemberAttribute> memberAttributes;
}

