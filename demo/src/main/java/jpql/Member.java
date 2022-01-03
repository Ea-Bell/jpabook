package jpql;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


@Entity

//NAmedQuery의 name은 클래스명.xxxx로 관리한다.
@NamedQuery(
	name="Member.findByUsername",
	query = "select m from Member m where m.username= :username"
)
public class Member {


	@Id
	@GeneratedValue
	private Long Id;
	private String username;
	private int age;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEAM_ID")
	private Team team;

	@Enumerated(EnumType.STRING)
	private MemberType type;

	public MemberType getType() {
		return type;
	}


	public void setType(MemberType type) {
		this.type = type;
	}


	//연관 관계 메서드
	public void changeTeam(Team team){
		this.team=team;
		team.getMember().add(this);
	}


	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@Override
	public String toString() {
		return "Member [Id=" + Id + ", age=" + age + ", username=" + username + "]";
	}


	
	
	
	
	
	
	
}
