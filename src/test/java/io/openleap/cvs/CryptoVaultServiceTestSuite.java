package io.openleap.cvs;

import io.openleap.cvs.controller.CryptoVaultControllerTest;
import io.openleap.cvs.service.CryptoServiceTest;
import io.openleap.cvs.util.AESUtilTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Comprehensive test suite for the Crypto Vault Service
 * 
 * This suite includes:
 * - Unit tests for AESUtil
 * - Unit tests for CryptoService
 * - Unit tests for CryptoVaultController
 * - Integration tests for CryptoVaultController endpoints
 * - Functional tests for end-to-end encryption/decryption flows
 */
@Suite
@SuiteDisplayName("Crypto Vault Service Test Suite")
@SelectClasses({
    AESUtilTest.class,
    CryptoServiceTest.class,
    CryptoVaultControllerTest.class
})
public class CryptoVaultServiceTestSuite {
    // This class serves as a test suite container
    // All test classes are selected via @SelectClasses annotation
}
