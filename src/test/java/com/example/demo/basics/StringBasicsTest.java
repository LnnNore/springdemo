package com.example.demo.basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * S3-2：String 的真相。
 *
 * <p>三个结论，全用断言证明：
 * <ol>
 *   <li>{@code ==} 比的是"是不是同一个对象"，{@code equals} 比的是"内容是否相同"</li>
 *   <li>String 不可变——任何"修改"都返回新对象</li>
 *   <li>循环里用 {@code +} 拼接会创建一大堆中间对象，要用 StringBuilder</li>
 * </ol>
 *
 * <p>运行方式：{@code mvn test -Dtest=StringBasicsTest}
 */
class StringBasicsTest {

    @Test
    @DisplayName("字面量会被常量池复用，所以 == 为 true")
    void stringLiteralsAreShared() {
        String a = "hello";
        String b = "hello";

        assertThat(a).isSameAs(b);      // isSameAs == ==，比引用
        assertThat(a).isEqualTo(b);     // isEqualTo == equals，比内容
    }

    @Test
    @DisplayName("new String 一定创建新对象，所以 == 为 false，但 equals 仍为 true")
    void newStringIsANewObject() {
        String a = "hello";
        String c = new String("hello");

        assertThat(a).isNotSameAs(c);   // 两个不同的对象
        assertThat(a).isEqualTo(c);     // 内容一样
    }

    @Test
    @DisplayName("intern() 会去常量池里拿同一个对象回来")
    void internReturnsThePooledInstance() {
        String c = new String("hi");
        assertThat(c).isNotSameAs("hi");

        assertThat(c.intern()).isSameAs("hi");   // 进了常量池就变成同一个
    }

    @Test
    @DisplayName("运行期拼接产生新对象（编译期常量折叠是另一回事）")
    void runtimeConcatenationCreatesNewObject() {
        String a = "he";
        String b = "llo";
        String c = a + b;           // 变量拼接 → 运行期，新对象

        assertThat(c).isNotSameAs("hello");
        assertThat(c).isEqualTo("hello");

        String d = "he" + "llo";    // 全是字面量 → 编译期就折叠成 "hello"
        assertThat(d).isSameAs("hello");
    }

    @Test
    @DisplayName("String 不可变：concat / toUpperCase 都返回新对象，原对象不动")
    void stringIsImmutable() {
        String s = "java";
        String upper = s.toUpperCase();
        String longer = s.concat("8");

        assertThat(s).isEqualTo("java");        // s 本身没变
        assertThat(upper).isEqualTo("JAVA");
        assertThat(longer).isEqualTo("java8");
        assertThat(s).isNotSameAs(longer);      // 是新对象
    }

    @Test
    @DisplayName("+ 在循环里会创建 N 个中间对象，StringBuilder 只创建一个缓冲区")
    void stringBuilderForLoops() {
        // 反例：每一步都 new 一个 String，1 万次就是 1 万个对象
        String bad = "";
        for (int i = 0; i < 5; i++) {
            bad += i;
        }

        // 正例：一个 StringBuilder，append 到最后才 toString()
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i);
        }

        assertThat(bad).isEqualTo(sb.toString()).isEqualTo("01234");
        // 结论：结果一样，但小数据看不出差别，数据量一大 + 拼接就是性能事故
    }

    @Test
    @DisplayName("trim 只去 ASCII 空白，strip 能去 Unicode 空白（Java 11+）")
    void trimVsStrip() {
        String s = "\u3000 hello \u3000";       // \u3000 是全角空格

        assertThat(s.trim()).isNotEqualTo("hello");   // trim 不认全角空格
        assertThat(s.strip()).isEqualTo("hello");     // strip 认
    }

    @Test
    @DisplayName("equals 要用常量在前，避免 null 触发 NPE")
    void nullSafeEquals() {
        String s = null;

        assertThat("admin".equals(s)).isFalse();      // ✅ 安全
        // s.equals("admin") 这样写会抛 NullPointerException
    }
}
