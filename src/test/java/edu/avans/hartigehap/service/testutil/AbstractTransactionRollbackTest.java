package edu.avans.hartigehap.service.testutil;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-root-context.xml")
@TransactionConfiguration(transactionManager = TestConstants.TX_MANAGER_NAME, defaultRollback = true)
@Transactional
public abstract class AbstractTransactionRollbackTest extends AbstractTransactionalJUnit4SpringContextTests {

}
