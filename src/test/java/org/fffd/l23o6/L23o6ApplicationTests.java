package org.fffd.l23o6;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.runners.Suite;

@RunWith(Suite.class) //使用JUnit的Suite运行器运行测试套件
@Suite.SuiteClasses({
        org.fffd.l23o6.integration.OrderIntegrationTest.class,
        org.fffd.l23o6.integration.RouteIntegrationTest.class,
        org.fffd.l23o6.integration.StationIntegrationTest.class,
        org.fffd.l23o6.integration.TrainIntegrationTest.class,
        org.fffd.l23o6.util.strategy.DiscountStrategyTest.class,
        org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategyTest.class,
        org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategyTest.class
})
@SpringBootTest
class L23o6ApplicationTests {

    // 这是一个空的测试套件类，不需要其他代码


}
