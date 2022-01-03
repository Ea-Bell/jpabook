package jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class JpqlMain {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {

			// ObjectOrientedQueryLanguage_10(em, emf);
			ObjectOrientedQueryLanguage_MiddleGrammar_11(em, emf);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
		emf.close();

	}

	private static void ObjectOrientedQueryLanguage_MiddleGrammar_11(EntityManager em, EntityManagerFactory emf) {
		
		//경로표현식(em, emf);
		//페치조인(em, emf);
		//다형성쿼리(em,emf);
		//엔티티직접사용(em, emf);
		//Named쿼리(em ,emf);
		  벌크연산(em, emf);

	}

	private static void 벌크연산(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 벌크 연산
		 *  재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?
		 *  JPA변경 감지 기능으로 실행하려면 너무 많은 SQL실행
		 *   1. 재고가 10개 미만인 상품을 리스트로 조회한다.
		 *   2. 상품 엔티티의 가격을 10% 증가한다.
		 *   3. 트랜잭션 커밋 시점에 변경감지가 동작한다.
		 *  변경된 데이터가 100건이라면 100번의 UPDATE SQL 실행됨.
		 * 
		 * 벌크 연산 예제
		 *  쿼리 한 번으로 여러 테이블 로우 변경(엔티티)
		 *  executeUpdate()의 결과는 영향받은 엔티티 수 반환
		 *  UPDATE, DELETE지원
		 *  INSERT(insert into .. select , 하이버 네이트 지원)
		 *  
		 * String qlString = "update Product p "+
		 * 					"set p.price = p.price *1.1 "+
		 * 					"where p.stockAmount < :stockAmount";
		 * int resultCount = em.createQuery(qlString).setParameter("stockAmount", 10). executeUpdate();
		 * 
		 * 벌크 연산 주의!!!!!!
		 *  벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리
		 *   벌크 연산을 먼저 실행!!!!!
		 *   벌크 연산 수행 후 영속성 컨텍스트 초기화!
		 */

		Team teamA = new Team();
		teamA.setName("teamA");
		em.persist(teamA);
	
		Team teamB = new Team();
		teamB.setName("teamB");
		em.persist(teamB);
	
		Member member1 = new Member();
		member1.setAge(10);
		member1.setUsername("회원1");
		member1.setTeam(teamA);
		em.persist(member1);
	
		Member member2 = new Member();
		member2.setAge(10);
		member2.setUsername("회원2");
		member2.setTeam(teamA);
		em.persist(member2);
	
		Member member3 = new Member();
		member3.setAge(10);
		member3.setUsername("회원3");
		member3.setTeam(teamB);
		em.persist(member3);
	
		em.flush();
		em.clear();

		int executeUpdate = em.createQuery("update Member m set m.age=20").executeUpdate();
		

		System.out.println("resultCount = " + executeUpdate);


		//띠용? 실제 값은 바뀌지 않았다.
		System.out.println("member1.getAge() = "+ member1.getAge());
		System.out.println("member2.getAge() = "+ member2.getAge());
		System.out.println("member3.getAge() = "+ member3.getAge());


		//새로 조회해도 값은 변하지 않는다.
		Member findMember = em.find(Member.class, member1.getId());
		System.out.println("member1.getAge() = "+ findMember.getAge());
		
		//새로 작업할려면 em.clear()을 하면된다.
		em.clear();
		Member findMember2 = em.find(Member.class, member1.getId()); //<< 다시 영속성을 시켜 작업을해야한다.
		System.out.println("member1.getAge() = "+ findMember2.getAge());
	}

	private static void 페치조인(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 실무에서 엄청나게 중요!!
		 *  페치조인(fetch join)
		 *  SQL 조인 종류 x
		 *  JPQL에서 성능 최적화를 위해 제공하는 기능
		 *  연관된 엔티티나 컬렉션을 SQL한 번에 함께 조회하는 기능
		 *  join fetch 명령어 사용
		 *  페치 조인 ::= [LEFT[OUTER]| INNER] JOIN FETCH 조인경로
		 */

		/**
		 * 엔티티 페치 조인
		 * 회원을 조회하면서 연관된 티도 함께 조회(SQL 한번에)
		 * SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 select 
		 * 
		 * [JPQL]
		 *  select m from Member m join fetch m.team
		 * 
		 * [SQL]
		 *  select M.*, T.* from Member m inner join Team t on m.Team_id=T.Id 
		 */

		/**
		 * 컬렉션 페치 조인
		 *  일대다 관계, 컬렉션 페치 조인
		 * 
		 * 	[JPQL] 
		 *  select t from Team t fetch t.members where t.name ='팀A'
		 * 
		 *  [SQL]
		 *  select T.*, M.* from Team T inner join Member M on T.ID = M.TEAM_ID where T.NAME='팀A'
		 */


		 /**
		  * 페치 조인과 일반 조인의 차이
		  * 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시로딩)
		  * 페치 조인은 객체 그래프를 SQL한번에 조회하는 개념 
		  */


		//페치조인1기본(em, emf);
		//페치조인2한계(em, emf);
	
	
	}

	private static void Named쿼리(EntityManager em, EntityManagerFactory emf) {
		/**
		 * Named쿼리 -정적 쿼리
		 *  미리 정의해서 이름을 부여해두고 사용하는 JPQL
		 *  "정적 쿼리"
		 *  어노테이션, XML에 정의
		 *  "애플리케이션 로딩 시점에 초기화 후 재사용"
		 *  "애프리케이션 로딩 시점에 쿼리를 검증"
		 */

		Team teamA = new Team();
		teamA.setName("teamA");
		em.persist(teamA);
	
		Team teamB = new Team();
		teamB.setName("teamB");
		em.persist(teamB);
	
		Member member1 = new Member();
		member1.setAge(10);
		member1.setUsername("회원1");
		member1.setTeam(teamA);
		em.persist(member1);
	
		Member member2 = new Member();
		member2.setAge(10);
		member2.setUsername("회원2");
		member2.setTeam(teamA);
		em.persist(member2);
	
		Member member3 = new Member();
		member3.setAge(10);
		member3.setUsername("회원3");
		member3.setTeam(teamB);
		em.persist(member3);
	
		em.flush();
		em.clear();

		 List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
		 					.setParameter("username", "회원1").getResultList();
			for (Member member : resultList) {
				System.out.println("member = " + member);
			}
	}

	private static void 엔티티직접사용(EntityManager em, EntityManagerFactory emf) {
	 
	/**
	 * 엔티티 직접 사용 - 기본 키 값
	 *  JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용
	 * 
	 * [JPQL]
	 *  select count(m.id) from Member m //엔티티의 아이디를 사용
	 *  select count(m) from Member m //엔티티를 직접 사용<<< JPQL에서
	 * 
	 * [SQL] (JPQL 둘다 같은 다음 SQL 실행)
	 *  select count(m.id) as cnt from Member m
	 * 
	 * 엔티티를 파라미터로 전달
	 *  String jpql ="select m from Member m where m = :member";
	 *  List resultList = em.createQuery(jpql).setParameter("member", member).getResultList();
	 * 
	 * 식별자를 직접 전달
	 *  String jpql ="select m from Member m where m.id =:memberId";
	 *  List resultList = em.createQuery(jpql).setParameter("memberId", memberId).getResultList();
	 * 
	 * 실행된 SQL
	 *  select m.*from Member m where =m.id=?
	 * 
	 * 엔티티 직접사용 - 외래 키값
	 *  
	 * 엔티티 파라미터로 전달
	 *  Team team =em. find(Team.class, 1L);
	 *  String qlString ="select m from Member m where m.team = :team";
	 *  List resultList = em.createQuery(qlString).setParameter("team", team).getResultList();
	 * 
	 * 식별자를 직접 전달
	 *  String qlString ="select m from MEmber m where m.team.id=:teamId";
	 *  List resultLsit = em.createQuery(qlString).setParameter("teamId", teamId).getResultList();
	 * 
	 * 실행된 SQL
	 *  select m.* from Member m where m.team_id=?
	 */

	Team teamA = new Team();
	teamA.setName("teamA");
	em.persist(teamA);

	Team teamB = new Team();
	teamB.setName("teamB");
	em.persist(teamB);

	Member member1 = new Member();
	member1.setAge(10);
	member1.setUsername("회원1");
	member1.setTeam(teamA);
	em.persist(member1);

	Member member2 = new Member();
	member2.setAge(10);
	member2.setUsername("회원2");
	member2.setTeam(teamA);
	em.persist(member2);

	Member member3 = new Member();
	member3.setAge(10);
	member3.setUsername("회원3");
	member3.setTeam(teamB);
	em.persist(member3);

	em.flush();
	em.clear();


	//기본키값으로넘김(em, emf,member1);
	외래키값으로넘김(em, emf, teamA);





	}

	private static void 외래키값으로넘김(EntityManager em, EntityManagerFactory emf, Team teamA) {
		String query = "select m from Member m where m.team = :team";
		List<Member> resultList = em.createQuery(query, Member.class).setParameter("team", teamA).getResultList();
		for (Member member : resultList) {
			System.out.println("Member = "+ member);
		}
	}

	private static void 기본키값으로넘김(EntityManager em, EntityManagerFactory emf, Member member1) {
			//엔티티를 파라미터로 전달
			String query ="select m from Member m where m = :member";
			List<Member> resultList = em.createQuery(query, Member.class).setParameter("member", member1).getResultList();
		   System.out.println("resultLsit = "+resultList);
		   //식별자를 직접 전달
		   String query2 ="select m from Member m where m.id = :memberId";
		   List<Member> resultList2 = em.createQuery(query2, Member.class).setParameter("memberId", member1.getId()).getResultList();
		  System.out.println("resultLsit = "+resultList2);
	}

	private static void 다형성쿼리(EntityManager em, EntityManagerFactory emf) {
	/**
	 * TYPE
	 *  조회 대상을 특정 자식으로 한정
	 *  예) Item 중에 Book, Movie를 조회해라
	 *  [JPQL]
	 *   select i from Item i where type(i) in(Book, Movie)
	 * 
	 *  [SQL]
	 *   select i from i where i.DTYPE in ('B', 'M')
	 * 
	 *  TREAT(JPA 2.1)
	 *  자바의 타입 캐스팅과 유사
	 *  상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
	 *  FROM, WHERE, SELECT(하이버네이트 지원)사용
	 *  예) 부모인 Item과 자식 Book이 있다.
	 *  
	 *  [JPQL]
	 *   select i from Item i where treat(i as Book).auther = 'kim'
	 * 
	 *  [SQL]
	 *   select i.* from Item i where i.DTYPE ='B' and i.auther ='kim'
	 */
	
	}

	private static void 페치조인2한계(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 페치 조인의 특징과 한계
		 *  페치 조인 대상에는 별치을 줄 수 없다
		 *   하이버네이트는 가능, 가급적 사용하지 않는다.
		 * 
		 *  둘 이상 컬렉션은 페치 조인 할 수 없다.
		 * 
		 *  컬렉션을 페치조인하면 페이징 API를 사용할 수 없다.
		 *    일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능
		 *    하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우위험!!!!!!!!!!!!!)
		 * 
		 *  연관된 엔티티들을 SQL한 번으로 조회 -성능 최적화
		 *  엔티티ㅔ 직접 적용하는 글로벌 로딩 전략보다 우선함
		 *   @OneToMant(Fetch=FetchType.LAZY)//글로벌 로딩 전략
		 *  실무에서 글로벌 로딩 전략은 모두 지연 로딩
		 *  최적화 필요한 곳은 페치 조인 적용
		 * 
		 */

		 /**
		  * 정리
		  *  모든 것을 페치 조인으로 해결할 수는 없음
		  *  페치 조인은 객체 그래프를 유지할 때 사용하면 효과적
		  *  여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면, 페치 조인보다 일반 조인을 사용하고 필요한 
		  *  데이터들만 조회해서 DTO로 반환하는 것이 효과적이다.
		  */


		Team teamA = new Team();
		teamA.setName("teamA");
		em.persist(teamA);

		Team teamB = new Team();
		teamB.setName("teamB");
		em.persist(teamB);

		Member member1 = new Member();
		member1.setAge(10);
		member1.setUsername("회원1");
		member1.setTeam(teamA);
		em.persist(member1);

		Member member2 = new Member();
		member2.setAge(10);
		member2.setUsername("회원2");
		member2.setTeam(teamA);
		em.persist(member2);

		Member member3 = new Member();
		member3.setAge(10);
		member3.setUsername("회원3");
		member3.setTeam(teamB);
		em.persist(member3);

		em.flush();
		em.clear();

		컬렉션테스트(em,emf);


	}

	private static void 컬렉션테스트(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 매우 위험!!
		 * WARN: HHH000104: firstResult/maxResults specified with collection fetch; applying in memory! << 메모리에 로드되었다고 경고 메시지뜸! 
		 * 그리고 페이지 쿼리가 나가지 않았다!
		 */

		// String query = "select t from Team t join fetch t.member m";
		// List<Team> resultList = em.createQuery(query, Team.class).setFirstResult(0).setMaxResults(10).getResultList();
		// System.out.println("result = "+ resultList);

		//해결방법! "다대일"로 해결하면됨!
		//  컬렉션 해결방법 -> BatchSize   LazyLoding으로 넘길때 BatchSize로 넘길 갯수를 정해서 넘기면 된다. 
		// BatchSize는 실무에서는 Global로 선언을 하기 떄문에 persistence.xml을 수정해준다.(1000개보다 적게 선언해주는게 팁임)
		// 3번째 방법 DTO로 새로 작성하면됨!
		String manyToOneQuery = "select t from Team t";
		List<Team> resultList = em.createQuery(manyToOneQuery, Team.class).setFirstResult(0).setMaxResults(2).getResultList();
		System.out.println("result = "+ resultList.size());
				for (Team team : resultList) {
				 System.out.println("team = " + team.getName()+", " +team.getMember().size());
					for(Member member : team.getMember()){
						System.out.println("-> member = " + member);
				}
		}
	}

	private static void 페치조인1기본(EntityManager em, EntityManagerFactory emf) {


		Team teamA = new Team();
		teamA.setName("teamA");
		em.persist(teamA);

		Team teamB = new Team();
		teamB.setName("teamB");
		em.persist(teamB);


		Member member1 = new Member();
		member1.setAge(10);
		member1.setUsername("회원1");
		member1.setTeam(teamA);
		em.persist(member1);


		Member member2 = new Member();
		member2.setAge(10);
		member2.setUsername("회원2");
		member2.setTeam(teamA);
		em.persist(member2);

		Member member3 = new Member();
		member3.setAge(10);
		member3.setUsername("회원3");
		member3.setTeam(teamB);
		em.persist(member3);

		em.flush();
		em.clear();

		
		// String query ="select m from Member m";
		// List<Member> resultList = em.createQuery(query,Member.class)
		// 				.getResultList();
		// 	for (Member member : resultList) {
		// 		System.out.println("member= "+ member.getUsername() + ", "+ member.getTeam().getName());
		// 		//회원1, 팀A(SQL)
		// 		//회원2, 팀A(1차캐시 영속성 컨텍스트에서 가져옴.)
		// 		//회원3, 팀B(SQL, 새로운 쿼리를 날림.)


		// 		//-> 회원 100명 -> N+1이 생김.
		// 	}

			//페치 조인으로 해결해야한다.
		// String fetchJoinQuery = "select m from Member m join fetch m.team";
		// List<Member> fetchResultList = em.createQuery(fetchJoinQuery, Member.class).getResultList();
		// for (Member member : fetchResultList) {
		// 	System.out.println("member = "+ member.getUsername() + ", "+ member.getTeam().getName());
		// }

	



		//컬렉션 페치 조인
		/**
		 * 1:다는 데이터가 뻥튀기 당할수 있다!
		 */

		// String collectionQuery = "select distinct t from Team t join fetch t.member";
		// List<Team> resultList = em.createQuery(collectionQuery, Team.class).getResultList();
		// for (Team team : resultList) {
		// 	System.out.println("team = " + team.getName()+", " +team.getMember().size());
		// 		for(Member member : team.getMember()){
		// 			System.out.println("-> member = " + member);
		// 		}
		// }

		/**
		 *  페치 조인과 DISTINCT
		 *  SQL의 DISTINCT는 중복된 결과를 제거하는 명령
		 *  JPQL의 DISTINCT 2가지 기능 제공
		 *   1. SQL에  DISTINCT추가
		 *   2. 애플리케이션에서 엔티티 중복 제거
		 *  
		 *  select distinct t from Team t jopin fetch t.members where t.name ='팀A'
		 *  SQL에 DISTINCT를 추가하지만 데이터가 다르므로 SQL결과에서 중복제거 실패(결과 같이 모두 같아야 DISTINCT가 발동됨!)
		 *  DISTINCT가 추가로 애플리케이션에서 중복 제거시도
		 *  같은 식별자를 가진 TEAM 엔티티 제거
		 */

		/**
		 * 페치 조인과 일반 조인의 차이
		 * 
		 * 일반조인 실행시 연관된 엔티티를 함께 조회하지 않음
		 * 
		 * [JPQL]
		 * select from Team t join t.members m where t.name ='팀A'
		 * 
		 * [SQL]
		 * select T.* from Team T inner join Member M on T.ID=M.TEAM_ID where T.NAME='팀A'
		 */

		//  String distinctQuery = "select distinct from Team t join fetch t.member";
		//  List<Team> resultList = em.createQuery(distinctQuery, Team.class).getResultList();
		//  for (Team team : resultList) {
		// 	 System.out.println("team");
		//  }

	}

	private static void 경로표현식(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 실무 조언
		 *  가급적 무시적 조인 대신에 명시적 조인 사용
		 *  조인은 sql 튜닝에 중요 포인트
		 *  묵시적 조인은 조인이 일어나는 상황을 한 눈에 파악하기 어렵다.
		 */
		
		
		/**
		 * 명시적 조인, 묵시적 조인
		 *  명시적 조인: join 키워드 직접 사용
		 *   select m from Member m join m.team t
		 *  묵시적 조인: 경로 표현식에 의해 묵시적으로 SQL조인 발생 (내부 조인만 가능)
		 * 
		 * select m.team from Member m
		 */

		/**
		 *  절대로 묵시적 조인 쓰지 말것.!!!!!!!!!!!!!!!!!!!!
		 * 경로 표현식
		 * .(점)을 찍어 객체 그래프를 탐색하는 것.
		 * select m.username -> 상태 필드
		 * from Member m
		 * join m.team t -> 단일 값 연관 필드
		 * join m.orders o -> 컬렉션 값 연관 필드
		 * where t.name ='팀A'
		 * 
		 * 경로 표현식 용어 정리
		 * 상태 필드(state field): eㅏㄴ순히 값을 저장하기 위한 필드 (ex: m.username)
		 * 연관 필드(association field): 연관관계를 위한 필드
		 * 단일 값 연관 필드
		 * @ManyToOne, @OneToOne, 대상이 엔티티(Ex: m.team)
		 * 컬렉션 값 연관 필드
		 * @OneToMany, @ManyToMany, 대상이 컬렉션(Ex:m.orders)
		 * 
		 * 경로 표현식 특징
		 * 상태 필드(state field): 경로 탐색의 끝, 탐색 X
		 * 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색 O
		 * 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색 X
		 * from 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
		 */

		 /**
		  * 경로 표현식 -예제
		  * select o.member.team from Order o -> 성공 ( 실행시 쿼리가 쉽게 예상하지 못한다.
		  *
		  * select t.members from Team -> 성공
		  *
		  * select t.members.username from Team t-> 실패
		  *
		  * select m.username from Team t join t.members m -> 성공 명시적조인
		  */

		  /**
		   * 경로 탐색을 사용한 묵시적 조인시 주의사항
		   *  항상 내부 조인
		   *  컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야함.
		   *  경로 탐색은 주로 select, where 절에서 사용하지만 묵시적 조인으으로 인해 SQL의 FROM(JOIN)절에 영향을 줌.
		   */



		Team team = new Team();
		em.persist(team);

		Member member1 = new Member();
		member1.setUsername("관리자1");
		member1.setTeam(team);
		em.persist(member1);

		Member member2 = new Member();
		member2.setTeam(team);
		member2.setUsername("관리자2");

		em.persist(member2);

		em.flush();
		em.clear();

		// StateField(em, emf);
		// SingleValuePaht(em, emf);
		CollectionValuePath(em, emf);

	}

	private static void CollectionValuePath(EntityManager em, EntityManagerFactory emf) {
		// String query = "select t.member from Team t";  //컬렉션 타입이라서 object로 받아야한다. 그런데 이렇게 받으면 안됨.
		

		// List<Collection> resultList = em.createQuery(query, Collection.class).getResultList();

		// for (Object team : resultList) {
		// 	System.out.println("team : "+ team);
		// }


		//이렇게 명확하게 줘야 알아먹기 편하니! 반드시 잊어버리지 말고 이렇게 써야함.
		String explicitQuery = "select m from Team t join  t.member m";
			List<Member> explicitResultList = em.createQuery(explicitQuery, Member.class).getResultList();

			for (Member team : explicitResultList) {
				System.out.println("team : "+ team);
			}
	}

	private static void SingleValuePaht(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 묵시적 내부조인
		 * 실제로 사용할때는 묵시적 내부조인을 사용하면 안된다.
		 * query튜닝할때 문제가 생기는 경우가 많아진다.
		 * JPQL이랑 sql이랑 문법이 비슷하게 적는게 좋다.
		 */
		String query = "select m.team from Member m";
		List<Team> resultList = em.createQuery(query, Team.class).getResultList();
		for (Team team : resultList) {
			System.out.println("s = " + team);
		}
	}

	private static void StateField(EntityManager em, EntityManagerFactory emf) {

		String query = "select m.username from Member m";
		List<String> resultList = em.createQuery(query, String.class).getResultList();

		for (String string : resultList) {
			System.out.println("s = " + string);
		}

	}

	private static void ObjectOrientedQueryLanguage_10(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시 플러시시점은 = commit과 sql문이 날라갈때 flush()가
		 * 나온다고함.
		 */

		// Jpql(em, emf);
		// QueryDSL(em, emf);//<<<이게 목표다.
		// Paging(em, emf);
		// Joins(em, emf);
		// SubQuery(em, emf);
		// JpqlTypeRepresent(em, emf);
		// ConditionalRepresent(em, emf);
		JpqlBaseFunction(em, emf);
	}

	private static void JpqlBaseFunction(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL 기본 함수
		 * CONCAT 문자열 더하기
		 * SUBSTRING 문자열을 x번 부터 x번 까지 짜르기.
		 * TRIM
		 * LOWER, UPPER
		 * LENGTH
		 * LOCATE
		 * ABS, SQRT, MOD
		 * SIZE, INDEX(JPA 용도)
		 */

		/**
		 * 사용자 정의 함수 호출
		 * 하이버네이트는 사용전 방언에 추가해야 한다.
		 * 사용하는 DB방언을 상속 받고, 사용자 저으이 함수를 등록한다.
		 * select function ('group_concat', i.name) from Item i
		 */

		// BaseFunction(em, emf);
		UserDefinedFunction(em, emf);
	}

	private static void UserDefinedFunction(EntityManager em, EntityManagerFactory emf) {
		// 각 DB에 선언된 사용자 정의 함수를 불러오는 거임!!! 만약 사용할꺼면 DB에 사용자 정의 함수 만들어서 불러와!!!!

		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member1 = new Member();
		member1.setUsername("관리자1");

		em.persist(member1);
		Member member2 = new Member();
		member2.setUsername("관리자2");

		em.persist(member2);

		em.flush();
		em.clear();

		String userFunction = "select group_concat(m.username) from Member m";
		List<String> resultList = em.createQuery(userFunction, String.class)
				.getResultList();
		for (String string : resultList) {
			System.out.println("s : " + string);
		}
	}

	private static void BaseFunction(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("관리자");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String conCatQuery = "select concat ('a','b') from Member m";
		String subStringQuery = "select substring(m.username, 2,3) from Member m";

		List<String> resultList = em.createQuery(subStringQuery, String.class).getResultList();
		// for (String string : resultList) {
		// System.out.println("s = " + string);
		// }

		/// 밑에는 interger형들.
		String locateQuery = "select locate('de','abcdefg') from Member m"; // >>반환값은 int로 위치값을 반환해준다.
		String sizeQuery = "select size(t.member) from Team t";
		List<Integer> intResultList = em.createQuery(sizeQuery, Integer.class).getResultList();
		for (Integer string : intResultList) {
			System.out.println("s = " + string);
		}

	}

	private static void ConditionalRepresent(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 조건식 -case 식
		 * 
		 * 기본 case 식
		 * select
		 * case when m.age <= 10 then '학생요금'
		 * when m.age >= 60 then '경로요금'
		 * else '일반요금'
		 * end
		 * from Member m
		 * 
		 * 단순 case 식
		 * select
		 * case t.name
		 * when '팀A' then '인센티브110%'
		 * when '팀B' then '인센티브120%'
		 * else '인센티브 105%'
		 * end
		 * from Team t
		 * 
		 * COALESCE: 하나씩 조회해서 nulldㅣ 아니면 반환
		 * 사용자 이름이 없으면 이름 없는 회원을 반환
		 * select coalesce(m.username, '이름 없는 회원') from Member m
		 * 
		 * NUllIF: 두 값이 같으면 null 반환, 다르면 첫번쨰 값 반환
		 * 사용자 이름이 '관리자'면 null을 반환하고 나머지는 본인의 이름을 반환
		 * select nullif(m.username, '관리자') from Member m
		 */

		// BaseIf(em, emf);
		// Coalesce(em, emf);
		Nullif(em, emf);

	}

	private static void Nullif(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("관리자");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		// 무언가를 숨길 때 nullif가 쓰인다.
		String query = "select nullif(m.username, '관리자') as username" +
				" from Member m";
		List<String> resultList = em.createQuery(query, String.class).getResultList();

		for (String string : resultList) {
			System.out.println("s = " + string);
		}
	}

	private static void Coalesce(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername(null);
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String query = "select coalesce(m.username, '이름 없는 회원') as username from Member m";

		List<String> resultList = em.createQuery(query, String.class).getResultList();
		for (String string : resultList) {
			System.out.println("s = " + string);
		}
	}

	private static void BaseIf(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("member1");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String query = "select " +
				"case when m.age <= 10 then '학생요금'" +
				"	  when m.age >= 60 then '경로요금'" +
				"	  else '일반요금' " +
				" end " +
				" from Member m";
		List<String> resultList = em.createQuery(query, String.class).getResultList();

		for (String s : resultList) {
			System.out.println("s = " + s);
		}

	}

	private static void JpqlTypeRepresent(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL 타입 표현
		 * 문자: 'HELLO', 'She''s'
		 * 숫자: 10L(Long), 10D(Double), 10F(Float)
		 * Boolean: TRUE, FALSE
		 * ENUM: jpabook.MemberType.Admin(패키지명 포함)
		 * 엔티티 타입: TYPE(m) = Member(상속 관계에서 사용)
		 */

		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("member1");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String query = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.type= jpql.MemberType.ADMIN";
		String errorQuery = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.type= jpql.MemberType.USER";

		List<Object[]> result = em.createQuery(errorQuery).getResultList();

		// for(Object[] objects: result){
		// System.out.println("object = " + objects[0]);
		// System.out.println("object = " + objects[1]);
		// System.out.println("object = " + objects[2]);

		// }

		// enumType은 이렇게 씁니다.
		String paramBind = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.type= :userType";
		List<Object[]> enumTypeList = em.createQuery(paramBind).setParameter("userType", MemberType.ADMIN)
				.getResultList();

		for (Object[] objects : enumTypeList) {
			System.out.println("object = " + objects[0]);
			System.out.println("object = " + objects[1]);
			System.out.println("object = " + objects[2]);

		}

		/**
		 * JPQL 기타
		 * SQL과 문법이 같은식
		 * EXISTS, IN
		 * AND, OR NOT
		 * =, >, >=, <, <=, <>
		 * BETWEEN, LIKE, IS NULL
		 */

		String jpqlEx = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.age between 0 and 10";
		List<Object[]> jpqlExList = em.createQuery(paramBind).setParameter("userType", MemberType.ADMIN)
				.getResultList();

		for (Object[] objects : jpqlExList) {
			System.out.println("object = " + objects[0]);
			System.out.println("object = " + objects[1]);
			System.out.println("object = " + objects[2]);

		}

	}

	private static void SubQuery(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 서브 쿼리
		 * 나이가 평균보다 많은 회원
		 * select m from Member m where m.age>(select avg(m2.age)from Member m2)
		 * 
		 * 한 건이라도 주문한 고객
		 * select m from Member m where(select count(o) from Order o hwere m =
		 * o.member)>0
		 */

		/**
		 * [NOT]EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
		 * {ALL | ANY | SOME} (subquery)
		 * ALL : 모두 만족하면 참
		 * ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
		 * [NOT] IN(subquery): 사브쿼리의 결과 중 하나라도 같은 것이 있으면 참
		 */

		/**
		 * 서브쿼리 -예제
		 * 
		 * 팀A 소속인 회원
		 * select m from Member m
		 * where exists(select t from m.team t where t.name ='팀A')
		 * 
		 * 전체 상품 각각의 재고보다 주문량이 많은 주문들
		 * select o from Order o
		 * where o.orderAmount >ALL(select p.stockAmount from Product p)
		 * 
		 * 어떤 팀이든 팀에 소속된 회원
		 * select m from Member m
		 * where m.team =ANY(select t from Team t)
		 */

		/**
		 * JPA 서브 쿼리 한계
		 * JPA는 where, having 절에서만 서브 쿼리 사용 가능
		 * select 절도 가능(하이버 네이트에서 지원)
		 * from 절의 서브쿼리는 현재 JPQL에서 불가능 >> 중요!
		 * 조인으로 풀 수 있으면 풀어서 해결
		 */

	}

	private static void Joins(EntityManager em, EntityManagerFactory emf) {

		Team team = new Team();
		team.setName("팀A");
		em.persist(team);

		Member member1 = new Member();
		member1.setUsername("회원1");
		member1.setTeam(team);
		em.persist(member1);

		Member member2 = new Member();
		member2.setUsername("회원2");



		em.flush();
		em.clear();

		/**
		 * 조인
		 * 내부 조인:
		 * select m from Member m [Inner] join m.team t
		 * 외부 조인:
		 * select m from Member m Left [Outer] Join m.team
		 * 세타 조인:(연관 관계 없을때 필요한 조인)
		 * select count(m) from Member m , Team t where m.username=t.name
		 */
		// Join(em, emf);

		/**
		 * 조인 on절
		 * 1. 조인 대상 필터링
		 * 2. 연관관계 없는 엔티티 외부 조인(하이버네이트 5.1부터)
		 * 
		 * 1_1 조인 대상 필터링
		 * 예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
		 * JPQL:
		 * select m, t from Member m left join m.team t on t.name='A'
		 * SQL:
		 * selecrt m.*, t.* from
		 * Member m left join Team t on m.TEAM_ID=t.id and t.name='A'
		 * 
		 * 2_1 연관관계 없는 엔티티 외부 조인
		 * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
		 * JPQL:
		 * select m, t from Member m left join Team t on m.username=t.name
		 * SQL:
		 * select m.*, t.* from Member m Left join Team t on m.username= t.name
		 * 
		 */

		// 조인 대상 필터링
		// JoinFilter(em, emf);

		// 연관 관계 없는 대상
		// NoAssociation(em, emf);

	}

	private static void Join(EntityManager em, EntityManagerFactory emf) {
		String innerJoin = "select m from Member m inner join m.team t ";
		String innerJoin2 = "select m from Member m join m.team t ";
		String leftOuterJoin = "select m from Member m left outer join m.team t";
		String leftOuterJoin2 = "select m from Member m left join m.team t";
		String setaJoin = "select m from Member m , Team t where m.username=t.name"; // cross라고 나옴!
		List<Member> result = em.createQuery(setaJoin, Member.class)
				.getResultList();

		System.out.println("result = " + result.size());
	}

	private static void NoAssociation(EntityManager em, EntityManagerFactory emf) {
		String noAssociation = "select m from Member m left join Team t on m.username=t.name";
		List<Member> onQueryResult = em.createQuery(noAssociation, Member.class)
				.getResultList();
		System.out.println("result :" + onQueryResult.size());
	}

	private static void JoinFilter(EntityManager em, EntityManagerFactory emf) {
		String onQuery = "select m from Member m left join m.team t on t.name= 'teamA'";
		List<Member> onQueryResult = em.createQuery(onQuery, Member.class)
				.getResultList();
		System.out.println("result :" + onQueryResult.size());
	}

	private static void Paging(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 페이징 API
		 * JPA는 페이징을 다음 두 API로 추상화
		 * setFirstResult(int start Position): 조회 시작 위치(0부터 시작)
		 * setMaxResults(int maxResult): 조회할 데이터 수
		 * 
		 * 현재 데이터 베이스 방언으로 돌아간다. persistence.xml의 hibernate.dialect의 value값을 바꾸면 각 DB로
		 * 맞춰서 쿠리를 짜서 보낸다.
		 */

		for (int i = 0; i < 100; i++) {
			Member member = new Member();
			member.setUsername("member" + i);
			member.setAge(i);
			em.persist(member);
		}
		em.flush();
		em.clear();

		List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
				.setFirstResult(1)
				.setMaxResults(10)
				.getResultList();

		System.out.println("result.size : " + result.size());

		for (Member member1 : result) {
			System.out.println("member1 =" + member1);
		}

	}

	private static void QueryDSL(EntityManager em, EntityManagerFactory emf) {

		/**
		 * QueryDSL 문자가 아닌 자바코드로 JPQL을 작성할 수 있음 JPQL빌더 역할 컴파일 시점에 문법 오류를 찾을 수 있음 동적쿼리 작성
		 * 편리함 단순하고 쉬움 실무 사용권장!
		 * http://querydsl.com/static/querydsl/5.0.0/reference/html_single/#jpa_integration
		 */

	}

	private static void Jpql(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리 SQL을 추상화해서 특정 데이터베이스 SQL에 의존X JPQL을 한마디로
		 * 정의하면 객체 지향 SQL
		 */

		/**
		 * JPQL문법 select m form Member as m where m.age>18 엔티티와 속성은 대소문자 구분O(Member,
		 * age) JPQL키워드는 대소문자 구분 x(select, from , where) 엔티티 이름 사용, 테이블 이름 아님(Member)
		 * 별칭은 필수(m)(as는 생략 가능)
		 */

		Member member = new Member();
		member.setUsername("member1");
		member.setAge(10);
		em.persist(member);

		// List<Member>result=em.createQuery("select m From Member m where m.name like
		// '%kim%'",Member.class)
		// .getResultList();
		//
		//
		// for(Member members : result) {
		// System.out.println("member = "+ members);
		// }

		// TypeQueryAndQuery(em, emf, member);
		// ParamiterBinding(em,emf, member);
		Projection(em, emf, member);

	}

	private static void Projection(EntityManager em, EntityManagerFactory emf, Member member) {
		/**
		 * 프로젝션 select 절에 조회할 대상을 지정하는 것 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터
		 * 타입) select m from Member m ->엔티티 프로젝션 select m.team from Member m -> 엔티티 프로젝션
		 * select m.address from Member m -> 임베디드 타입 프로젝션 select m.username, m.age from
		 * Member m -> 스칼라 타입 프로젝션 DISTINCT로 중복 제거
		 */

		/**
		 * 
		 */

		/*
		 * 이렇게 묵시적으로 적으면 안된다 이유: 다른 사람이 Join이 쓰는지 안쓰는지 모르기 때문. List<Team> result =
		 * em.createQuery("select m.team from Member m", Team.class).getResultList();
		 */

		// List<Team> result = em.createQuery("select t from Member m Join m.team
		// t",Team.class).getResultList();//<<이렇게 명시적으로 적어야함.

		// em.createQuery("select o.address from Order o",
		// Address.class).getResultList();

		// em.createQuery("select distinct m.username, m.age from Member
		// m").getResultList(); //distinct

		ProjectionMulitValueSelect(em, emf, member);

	}

	private static void ProjectionMulitValueSelect(EntityManager em, EntityManagerFactory emf, Member member) {
		/**
		 * 여러값 조회
		 * Select m.username, m.age from Member m
		 * 1. Query 타입으로 조회
		 * 2. Object[]타입으로 조회
		 * 3. new 명령어로 조회
		 * 단순 값을 DTO로 바로 조회
		 * select new jpabook.jpql.UserDTO(m.username, m.age)from Member m
		 * 패키지명을 포함한 전체 클래스 명 입력
		 * 순서와 타입이 일치하는 생성자 필요
		 */

		/**
		 * Query 타입으로 조회
		 */
		// List resultList = em.createQuery("select m.username, m.age from Member m")
		// .getResultList();
		// Object o = resultList.get(0);
		// Object [] result = (Object[])o;
		//
		// System.out.println("username = "+ result[0]);
		// System.out.println("result = "+ result[1]);

		/**
		 * Objet[] 타입으로 조회
		 */
		// List<Object[]> resultList2 = em.createQuery("select m.username, m.age from
		// Member m").getResultList();
		//
		// Object[] result2 = resultList2.get(0);
		//
		// System.out.println("username = "+ result2[0]);
		// System.out.println("result = "+ result2[1]);

		/**
		 * new 타입으로 조회
		 */

		em.createQuery("select new jpql.MemberDTO(m.username, m.age)from Member m", MemberDTO.class);
	}

	private static void ParamiterBinding(EntityManager em, EntityManagerFactory emf, Member member) {
		/**
		 * 파라미터 바인딩- 이름 기준, 위치 기준.
		 * 
		 */
		TypedQuery<Member> query = em.createQuery("select m from Member m where m.username=:username", Member.class);
		query.setParameter("username", "member1");
		Member singleResult = query.getSingleResult();
		System.out.println("singleResult = " + singleResult);

	}

	private static void TypeQueryAndQuery(EntityManager em, EntityManagerFactory emf, Member member) {
		/*
		 * TypeQuery: 반환 타입이 명확할 때 사용 Query: 반환 타입이 명확하지 않을 때 사용
		 */
		TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
		TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
		Query query3 = em.createQuery("select m.username, m.age from Member m", Member.class);

	}
}
