package com.corestack.backend.expense.entity;

import com.corestack.backend.auth.entity.UserEntity;
import com.corestack.backend.common.entity.BaseAuditEntity;
import com.corestack.backend.expense.enums.SplitType;
import com.corestack.backend.group.entity.GroupEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sw_expenses")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private UserEntity paidBy;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_type", nullable = false, length = 20)
    private SplitType splitType;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseSplitEntity> splits = new ArrayList<>();
}
