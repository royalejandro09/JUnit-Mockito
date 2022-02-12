package org.junit5app.ejemplo.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit5app.ejemplo.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Forzando que la Instancia de la clase CuentaTest, se cree una sola vez y no por cada
 * metodo (Llamada a un metodo Test).
 * <p>
 * No es buena practica tener una sola Instancia de cuentaTest, ya que invita a tener
 * dependencias entre metodos.
 *
 * @TestInstance(TestInstance.Lifecycle.PER_CLASS)
 */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    private TestInfo info;
    private TestReporter reporter;

    @BeforeEach
    void initMetodoTest(TestInfo info, TestReporter reporter) {
        this.cuenta = new Cuenta("Roy", new BigDecimal("1000.0"));
        this.info = info;
        this.reporter = reporter;

        System.out.println("Iniciando el metodo");
        reporter.publishEntry("Ejecutando: " + info.getDisplayName() + " " + info.getTestMethod().orElse(null).
                getName() + " con las etiquetas " + info.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo.");
    }

    /**
     * Metodos de Clase(Static) pertenece a la clase no de Instancia CuentaTest{}.
     * Se crea antes de que se cree la instancia de class CuentaTest{}.
     * @BeforeAll & @AfterAll
     */
    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando la prueba test😎");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando la prueba test😁");
    }


    /**
     * Class anidada cuentaTestNombreSaldo{}
     */
    @Tag("cuenta")
    @Nested
    @DisplayName("probando atributos de la cuenta")
    class CuentaTestNombreSaldo {
        /**
         * Afirmacion si es igual
         * assertEquals(Expected, actual);
         * assertTrue(Expected.equals(actual);
         */
        @Test
        @DisplayName("la cuenta")
//        Injection of dependency en los argumentos que recibe el metodo
        void testNombreCuenta() {
            reporter.publishEntry(info.getTags().toString());
            if (info.getTags().contains("cuenta")) {
                reporter.publishEntry("Hacer algo con esa etiqueta");
            }

//        cuenta = new Cuenta("Roy", new BigDecimal("1000.12345"));
//        cuenta.setPersona("Roy");

            String esperado = "Roy";
            String recibido = cuenta.getPersona();
            assertNotNull(recibido, () -> "La cuenta no puede ser Nula");
            assertEquals(esperado, recibido, () -> "El nombre de la cuenta no es el que se esperaba");
            assertTrue(recibido.equals(esperado), () -> "El nombre recibido  debe ser igual al real/esperado");
        }

        @Test
        @DisplayName("Saldo de la cuenta corriente que no sea Null, Mayor que 0, valor esperado😋.")
        void testSaldoCuenta() {
//        cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12345"));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.0, cuenta.getSaldo().doubleValue());
            /**
             * En la expresion " cuenta.getSaldo().compareTo(BigDecimal.ZERO)"
             * Esta me va a retornar un -1, 0, O 1
             * Posteriormente se va a evaluar ese return "-1" con el < 0
             */
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Comparando datos de las cuentas")
        void testReferenciaCuenta() {
//        cuenta = new Cuenta("Alejandro", new BigDecimal("12345.123"));
            Cuenta cuentaAleterna = new Cuenta("Roy", new BigDecimal("1000.0"));

            /**
             * Con el metodo equals() en la clase cuenta
             * Se hace la comparacion de los datos de los objetos para saber si son iguales.
             */
//      assertNotEquals(cuentaAleterna, cuenta);
            assertEquals(cuentaAleterna, cuenta);

        }
    }

    /**
     * Class CuentaOperacionesTest{}
     */
    @Nested
    class CuentaOperacionesTest {
        @Tag("cuenta")
        @Test
        @DisplayName("Probando el debito de la cuenta")
        void testDebitoCuenta() {
//        cuenta = new Cuenta("Richard", new BigDecimal("1000.0"));
            cuenta.debito(new BigDecimal(100));
//        System.out.println(cuenta.getSaldo());

            assertNotNull(cuenta.getSaldo());
            assertEquals(900.0, cuenta.getSaldo().intValue());
            assertEquals("900.0", cuenta.getSaldo().toPlainString());

        }

        @Tag("cuenta")
        @Test
        @DisplayName("Probando el credito de la cuenta")
        void testCreditoCuenta() {
//        cuenta = new Cuenta("Roy", new BigDecimal("1000.0"));
            cuenta.credito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100.0, cuenta.getSaldo().intValue());
            assertEquals("1100.0", cuenta.getSaldo().toPlainString());

        }

        @Tag("banco")
        @Tag("cuenta")
        @Test
        @DisplayName("Probando transferencia de dinero")
        void testTransferirDineroCuentas() {
            Cuenta cuentaFanny = new Cuenta("Fanny", new BigDecimal("5000"));
            Cuenta cuentaRichard = new Cuenta("Richard", new BigDecimal("10000"));

            Banco banco = new Banco();
            banco.setNombre("Banco de la Rpublica");

            banco.transferir(cuentaRichard, cuentaFanny, new BigDecimal("5000"));

            assertEquals("5000", cuentaRichard.getSaldo().toPlainString());
            assertEquals("10000", cuentaFanny.getSaldo().toPlainString());

        }
    }

    @Tag("cuenta")
    @Tag("error")
    @Test
    @DisplayName("Lanzando Excepcion DineroInsuficienteException")
    void testDineroInsuficienteException() {
//        cuenta = new Cuenta("Roy", new BigDecimal("100.0"));

        /**
         *  assertThrows (DineroInsuficienteException.class, () ->
         *
         *  espera lanzar la excepcion, si se lanza
         *  se cumple la condicion (asserThrows) y la prueba pasa, si no, la prueba falla.
         */
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1500.0"));
        }, () -> "El saldo NO es insuficiente");

        String actual = exception.getMessage();
        String esperadoMsj = "Dinero Insuficiente";

        assertEquals(esperadoMsj, actual);

    }

    @Tag("cuenta")
    @Tag("banco")
    @Test
//    @Disabled
//    @DisplayName("Probando la relacion de Banco con las Cuentas con assertAll")
    void testRelacionBancoCuetas() {
//        fail();
        Cuenta cuentaFanny = new Cuenta("Fanny", new BigDecimal("5000"));
        Cuenta cuentaRichard = new Cuenta("Richard", new BigDecimal("10000"));

        Banco banco = new Banco();
        banco.setNombre("Banco de la Rpublica");

        /**
         * Metodo Transferir
         * Cuenta origen, cuenta Destino, monto.
         */
        banco.transferir(cuentaRichard, cuentaFanny, new BigDecimal("10000"));

        /**
         * Agrgo las cuentas a la lista del Banco
         */
        banco.addCuentas(cuentaFanny);
        banco.addCuentas(cuentaRichard);

        /**
         * assertAll()->{}
         */
        assertAll(() -> {
                    assertEquals("0", cuentaRichard.getSaldo().toPlainString(), () -> "El valor del saldo de la " +
                            "cuenta debe ser 0");
                },
                () -> {
                    assertEquals("15000", cuentaFanny.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Banco de la Rpublica", cuentaRichard.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Banco de la Rpublica", cuentaFanny.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Fanny", banco.getCuentas().stream().
                            filter(c -> c.getPersona().equals("Fanny")).
                            findFirst().
                            get().
                            getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream().
                            anyMatch(c -> c.getPersona().equals("Richard")));
//                filter(c -> c.getPersona().equals("Richard")).
//                findFirst().isPresent());

                });
    }

    /**
     * Clases de test Anidados utilizando:
     *
     * @Nested
     */
    @Nested
    class SsietemaOperativoTest {

        /**
         * Pruebas condicionales.
         * Se ejecutan solo en ciertos escenarios.
         * Enabled/Disabled(Activar/Desactivar)
         */
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {

        }

        @Test
        @EnabledOnOs({OS.MAC, OS.LINUX})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }

    }

    /**
     * Class Anidada javaVersionTest{}
     */
    @Nested
    class javaVersionTest {

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void soloJdk17() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_17)
        void testNoJdk17() {
        }

    }

    /**
     * Class anidada SystemPropertiesTest{}
     */
    @Nested
    class SystemPropertiesTest {
        /**
         * Test conditionales with Properties of System.
         */
        @Test
        void imprimirSystemProperties() {
            Properties propertie = System.getProperties();
            propertie.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "17.1.1")
        void testJvaVersion() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*65.*")
        void testNo64() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }

    }

    /**
     * Class anidada VariableAmbienteTest{}
     */
    @Nested
    class VariableAmbienteTest {
        /**
         * Test conditionales with Variables de ambiente.
         */
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.0.1.*")
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {
        }

    }


    /**
     * Test conditional with Assumptions(Suposiciones)
     * de forma Programatica.
     */
    @Test
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        // Si la condicion es Falsa, se desabilita el metodo.
        assumeTrue(esDev);

        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.0, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * Con el assumingThat() el metodo no se desabilita, solo se desabilita el codigo dentro
     * del assumingThat() simpre y cuando no pase el test.
     * El codigo que esta fuera del assumingThat() simpre se ejecutara.
     */
    @Test
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
//            assertEquals(1100.0, cuenta.getSaldo().doubleValue());
        });
//        assertEquals(1100.0, cuenta.getSaldo().doubleValue());
    }

    /**
     * Class Paremetrizada
     */
    @Tag("param") //Etiquetas para purebasTest asi poder ejecutar las que tengan la misma etiqueta.
    @Nested
    class PruebasParametrizadasTest {
        /**
         * Espcribiendo metodos Parametrizados con la anotacion
         *
         * @ParameterizedTest Entrada de parametros con
         * @ValueSource
         * @CsvSource
         * @CsvFileSource
         * @MethodSource
         */
        @ParameterizedTest(name = "Number {index} with value {0} - {argumentsWithNames}")
        @ValueSource(doubles = {100, 200, 500, 750, 100})
//    @ValueSource(Strings = {"100", "200", "500", "750", "993.5"})
        void testDebitoCuentaValueSource(Double monto) {
//        Como parametro del metodo recibe un Objeto (BigDecimal) y recibe valores String, Int o Double
            cuenta.debito(new BigDecimal(monto));
            if (monto == 1000.1) {
                System.out.println(cuenta.getSaldo());
            }
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1, 100", "2, 250", "3, 500", "4, 750", "5, 990"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200, 100, roy, roy", "250, 200, pepe, pepe", "360, 350, pablo, pablo", "800, 750, diego, diego", "1000, 990,pedro, pedro"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, cuenta.getPersona());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
//    Se debe crear el Archivo "/data.csv" en resources el cual debe contener los parametros.
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.setPersona(actual);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, cuenta.getPersona());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        /**
         * Repetir Test
         *
         * @param info
         * @RpeatedTest(number)
         * @RepeatedTest(value= 5, name ="")
         */
        @RepeatedTest(value = 5, name = "Repeat number {currentRepetition} de {totalRepetitions}")
//    @RepeatedTest(5)
//        RepetitionInfo -> Es como una injection of dependency en el parametro del metodo
        void testDebitoCuentaRepeat(RepetitionInfo info) {

//        Me permite manipular la prueba en la repeticion elegida con el parametro RepetitionInfo
            if (info.getCurrentRepetition() == 3) {
                System.out.println("Repeticion 3");
            }
            if (info.getCurrentRepetition() == 5) {
                System.out.println("Finaliza la prueba " + info.getTotalRepetitions());
            }
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900.0, cuenta.getSaldo().intValue());
            assertEquals("900.0", cuenta.getSaldo().toPlainString());

        }

    }

    /**
     * Utilizando la anotacion @RepatedTest
     * Repite pruebas cuenado el valor es aleatorio/Random
     * Metodo estatico debe estar en la Raiz.
     */
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * Creando el metodo para la creacion de la lista
     * que sera consumida por el metodo anterior testDebitoCuentaMethodSource()
     *
     * @return
     */
    private static List<String> montoList() {
        return Arrays.asList("100", "200", "500", "750", "900");
    }


    /**
     * Class TimeOutTest
     */
    @Nested //Clas Anidada
    @Tag("timeout") //Etiqueta de la class
    class PruebaTimeOut {

        @Test
        @Timeout(3)
            //TimeOut por default en Seconds
        void timeOutTest() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }

        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
        void timeOutTest2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(400);
        }

        @Test
        void timeOutAssertions() throws InterruptedException {
            assertTimeout(Duration.ofSeconds(1), () -> {
                TimeUnit.MILLISECONDS.sleep(900);
            });
        }
    }


}