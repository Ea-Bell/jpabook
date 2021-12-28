package jpabook.jpashop.domain;

import java.util.ArrayList;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Address address;
    private DeliveryStatus status;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

	private Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	private Address getAddress() {
		return address;
	}

	private void setAddress(Address address) {
		this.address = address;
	}

	private DeliveryStatus getStatus() {
		return status;
	}

	private void setStatus(DeliveryStatus status) {
		this.status = status;
	}

	private Order getOrder() {
		return order;
	}

	private void setOrder(Order order) {
		this.order = order;
	}
    
    
    
    
}
