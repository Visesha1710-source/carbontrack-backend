
package com.carbontrack.activity;

import com.carbontrack.user.AppUser;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate activityDate;
    private String name;
    private String transport;
    private double electricity;
    private String food;
    private double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public Activity() {}

    public Long getId() { return id; }
    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }
    public double getElectricity() { return electricity; }
    public void setElectricity(double electricity) { this.electricity = electricity; }
    public String getFood() { return food; }
    public void setFood(String food) { this.food = food; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
}
