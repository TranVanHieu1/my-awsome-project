package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findAccountByName(String name);

    // ---- <UC 29: View list of students/instructors> ----
    List<Account> findByRole(AccountRoleEnum role);

    List<Account> findByRoleAndStatus(AccountRoleEnum role, AccountStatusEnum status);
    // ---- </UC29> ---------


    //------- <UC 30: Approve or Reject Instructor registration> ------
    Account findByIdAndRole(Integer id, AccountRoleEnum role);

    List<Account> findByStatus(AccountStatusEnum status);

    Account findInstructorById(Integer instructorID);

    List<Account> findByRoleAndStatusAndIsDeleted(AccountRoleEnum role, AccountStatusEnum status, boolean b);

    Account findByIdAndRoleAndIsDeleted(Integer accountId, AccountRoleEnum accountRoleEnum, boolean b);

    List<Account> findByRoleAndIsDeleted(AccountRoleEnum role, boolean b);

    List<Account> findByStatusAndIsDeleted(AccountStatusEnum status, boolean b);
    //------- </UC30> --------------

}
