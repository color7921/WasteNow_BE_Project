package edu.pnu.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer postId;
	private String title;
	private String content;
	private String image;
	@Builder.Default
	private Integer count = 0;
	private String tag;
	@ManyToOne
	@JoinColumn(name = "big_id")
	private BigTrash bigTrash;
	@ManyToOne
	@JoinColumn(name = "username")
	private Member member;
	
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createDate = new Date();
	
}
