package com.riseup.flimbit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.dto.ShareTypeDTO;
import com.riseup.flimbit.entity.ShareType;

import java.util.List;

public interface ShareTypeRepository extends JpaRepository<ShareType, Long> {
    List<ShareType> findByMovieId(Long movieId);
    
    @Query(value = " select st.* , sum(mi.number_of_shares) as soldShare"
    		+",sum(mi.amount_invested) as soldAmount "
    		+",ROUND(SUM(mi.amount_invested) * st.company_commission_percent / 100.0, 2) as companyCommission"
    		+ ",ROUND(SUM(mi.return_amount) * st.profit_commission_percent / 100.0, 2) as companyProfitCommission"
    		+",mo.budget as budget"
    		+",mo.total_shares as totalShare"
    		+",mo.per_share_amount as perShareAmount"
    		+" from share_type  st"
    		+" left join movies_investment mi on mi.share_type_id = st.id"
    		+" left join movies mo on mo.id = st.movie_id"
            +"  where st.movie_id = :movId"
    		+" group by st.name , mo.id,st.id ",nativeQuery = true)
    List<ShareTypeDTO>  getShareTypeByMovieId(@Param("movId") long movieId);
    
}
