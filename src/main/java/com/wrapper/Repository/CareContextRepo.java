//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.Repository;

import com.wrapper.Model.CareContextTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareContextRepo extends MongoRepository<CareContextTable, String> {
	CareContextTable findByInitRequestId(String onInitRequestId);

	CareContextTable findByConfirmRequestId(String confirmRequestId);

	CareContextTable findByCareContextRequestId(String careContextRequestId);
}
