package jpabook.jpashop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ManyToAny;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    //기간 Period
    @Embedded
    private Period period;
    
    //주소
    @Embedded
    private Address address;
    
    
    @ElementCollection
    @CollectionTable(name="FAVORITE_FOOD", 
    				joinColumns = @JoinColumn(name="MEMBER_ID"))
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods= new HashSet<>();
    
    
    
//    @ElementCollection
//    @CollectionTable(name = "ADDRESS", 
//    				joinColumns = @JoinColumn(name= "MEMBER_ID"))
//    private List<Address> addressesHistory= new ArrayList<>();
//    
 
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressesHistory = new ArrayList<>();
    
	public Set<String> getFavoriteFoods() {
		return favoriteFoods;
	}



	public void setFavoriteFoods(Set<String> favoriteFoods) {
		this.favoriteFoods = favoriteFoods;
	}



	public List<AddressEntity> getAddressesHistory() {
		return addressesHistory;
	}



	public void setAddressesHistory(List<AddressEntity> addressesHistory) {
		this.addressesHistory = addressesHistory;
	}



	public Period getPeriod() {
		return period;
	}



	public void setPeriod(Period period) {
		this.period = period;
	}



	public Address getAddress() {
		return address;
	}



	public void setAddress(Address address) {
		this.address = address;
	}



	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

    
    
    public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
