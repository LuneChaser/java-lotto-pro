package stringcalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.junit.jupiter.api.Nested;

public class StringCalculatorTest {
  @DisplayName("주어진 문자열에대해 합계를 계산한다.")
  @CsvSource({"'1,2', 3",
              "'2:3', 5",
              "'2,3:5', 10",
              "'//!\n1!5', 6",
              "'//!\n1!5!10', 16",
              "'//!\n1!5,7', 13",
              })
  @ParameterizedTest(name = "{index} => String Value [{0}] sumValue [{1}]")
  void calculate(String value, Integer expectedValue) {
    // given
    StringCalculator stringCalculator = new StringCalculator();

    // when
    Integer realValue = stringCalculator.calculate(value);

    // then
    assertThat(realValue).isEqualTo(expectedValue);
  }

  @DisplayName("문자열계산기의 파서가 ")
  @Nested
  class StringCalculatorParserTest {
    @DisplayName("숫자와 기본 구분자로 구성된 문자열을 받을시 합계가 계산된다.")
    @CsvSource({"'1,2', 3",
                "'4:5', 9",
                "'4:5:1', 10",
                "'3,4:5:1', 13"
              })
    @ParameterizedTest(name = "{index} => String Value [{0}] sumValue [{1}]")
    void getCalculatorNumber(String value, Integer expectedValue) {
      // given
      Parser stringParser = new Parser();

      // when
      Numbers realCalculatorNumbers = stringParser.parse(value);

      // then
      assertThat(realCalculatorNumbers.sum()).isEqualTo(expectedValue);
    }

    @DisplayName("숫자와 커스텀 구분자로 구성된 문자열을 받을시 합계가 계산된다.")
    @CsvSource({"'//|\n1|2', 3",
                "'//[\n4[5', 9",
                "'//|\n4,5|1', 10"
              })
    @ParameterizedTest(name = "{index} => String Value [{0}] sumValue [{1}]")
    void getCalculatorNumber_custrom(String value, Integer expectedValue) {
      // given
      Parser stringParser = new Parser();

      // when
      Numbers realCalculatorNumbers = stringParser.parse(value);

      // then
      assertThat(realCalculatorNumbers.sum()).isEqualTo(expectedValue);
    }
  }

  @DisplayName("구분자들에대해")
  @Nested
  class SeperatorsTest {
    @DisplayName("저장된 값이 문자열 결합으로 반환된다.")
    @Test
    void return_allSeperator() {
      // given
      List<Separator> tempSeparators = new ArrayList<>();
      tempSeparators.add(new Separator(","));
      tempSeparators.add(new Separator(":"));

      // when
      Separators separators = new Separators(tempSeparators);

      // then
      assertThat(separators.value()).isEqualTo(",:");
    }

    @DisplayName("기본구분자가 있을 때 추가적으로 커스텀 구분자가 추가된다.")
    @Test
    void add_seperatorWithDefault() {
      // given
      List<Separator> tempSeparators = new ArrayList<>();
      tempSeparators.add(new Separator(","));
      tempSeparators.add(new Separator(":"));

      Separators separators = new Separators(tempSeparators);

      // when
      separators.add(new Separator("@"));

      // then
      assertThat(separators.value()).isEqualTo(",:@");
    }
  }

  @DisplayName("계산할 숫자들이")
  @Nested
  class CalculratorNumbersTest {
    @DisplayName("여러개일 경우 각 값의 합계를 반환하게된다.")
    @Test
    void  sum_multiValue() {
      // given
        List<Number> tempCalculratorNumbers = new ArrayList<>();
        tempCalculratorNumbers.add(new Number("2"));
        tempCalculratorNumbers.add(new Number("1"));

        Numbers calculatorNumbers = new Numbers(tempCalculratorNumbers);

      // when
        Integer realValue = calculatorNumbers.sum();

      // then
        assertThat(realValue).isEqualTo(3);
    }

    @DisplayName("없을 경우 0을 반환하게된다.")
    @Test
    void sum_none() {
      // given
        List<Number> tempCalculratorNumbers = new ArrayList<>();

        Numbers calculatorNumbers = new Numbers(tempCalculratorNumbers);

      // when
        Integer realValue = calculatorNumbers.sum();

      // then
        assertThat(realValue).isZero();
    }
  }

  @DisplayName("계산할 숫자가")
  @Nested
  class CalculratorNumberTest {
    @DisplayName("음수이거나 숫자가 아닐경우 RuntimeException이 발생된다.")
    @ValueSource(strings = {"-1", "a", "A", "!", "|"})
    @ParameterizedTest(name = "{index} => String Value [{0}]")
    void numberInvalid(String value) {
      // given

      // when
      ThrowingCallable exceptionContent = () -> new Number(value);

      // then
      assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(exceptionContent);
    }
  }
}