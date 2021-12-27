package jpabook.jpashop;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Member;


public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

          Member member1 = new Member();
          member1.setName("member1");
          em.persist(member1);
          
          Member member2 = new Member();
          member2.setName("member2");
          em.persist(member2);
          
          
          em.flush();
          em.clear();
          //
          //Member findMember = em.find(Member.class, member.getId());
          Member m1= em.find(Member.class, member1.getId());// Member
          Member m2= em.getReference(Member.class, member2.getId());// Proxy
    
          logic(m1, m2);

          /**
           * 새로 하는 이유
           * member 1, 2는 위에 쓰고있어서  다시 가져오면 객체를 불러오기때문에
           * 새로 선언해줬다.
           */
          
         Member member3 = new Member();
         Member member4 = new Member();
         em.persist(member3);
         em.persist(member4);
         
         em.flush();
         em.clear();
         Member m1_1=em.getReference(Member.class, member3.getId());
         Member reference = em.find(Member.class, member3.getId());
                 
         ValueCompare(m1_1, reference);

         NoManaged(em, emf);
         

    
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }

    
	private static void NoManaged( EntityManager em, EntityManagerFactory emf) {
		 //================================== 영속성 관리 안할시.
	     
        Member member5 = new Member();
        em.persist(member5);

        em.flush();
        em.clear();
        
        
        Member refMember= em.getReference(Member.class, member5.getId());
       
        System.out.println("isLoaded= "+ emf.getPersistenceUnitUtil().isLoaded(refMember));

        refMember.getName(); //강제 호출로 영속성 추가.
		em.detach(refMember);
		
		System.out.println("isLoaded= "+ emf.getPersistenceUnitUtil().isLoaded(refMember));
		
		//System.out.println("detach된 reference = "+ refMember.getName()); //관리가 안되므로 에러가 나온다.
		
	
	}


	public static void ValueCompare(Member m1_1, Member reference) {
		
		/**
		 *  키 포인트! 
		 *  getReference의 반환된 클래스와
		 *  find의 반환된 클래스는 같다!
		 */
		
		System.out.println("reference = " + reference.getClass());
		System.out.println("m1_1 = " + m1_1.getClass());
		
		//============================= 테스트 결과내용
		
		/*
		 *  reference와 m1_1은 결국엔 같은 프록시 값이 반환 된다.(같은 class가 반환됨.) 
		 */		
		System.out.println("a == a : "+ (m1_1== reference));
	}

	public static void logic(Member m1,Member m2) {
		
		/**
		 * Proxy또는 영속성으로 관리되는 객체를 instanceof로 비교를 해야한다.
		 */
		
		System.out.println("m1 == Member : "+ (m1 instanceof Member));
		System.out.println("m2 == Member : "+ (m2 instanceof Member));
		System.out.println("m1 == m2 : "+ (m1 == m2));
		
		
		
	}
}
