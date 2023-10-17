package org.apostolis;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({OperationsServiceImplTest.class,
                UserServiceImplTest.class,
                ViewsTest.class})
public class TestSuite {
}
