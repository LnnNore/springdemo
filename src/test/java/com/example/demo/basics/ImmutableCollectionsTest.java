package com.example.demo.basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * S3-1：不可变集合。
 *
 * <p>这一组测试不是"教程"，是<b>可执行的证据</b>。每条断言都在证明一个你面试会被问到的结论。
 * 直接运行，看着它变绿，比读十篇博客有用。
 *
 * <p>运行方式（在 D:\Projects\springdemo 下）：
 * <pre>mvn test -Dtest=ImmutableCollectionsTest</pre>
 */
class ImmutableCollectionsTest {

    @Test
    @DisplayName("List.of 创建的集合是真正不可变的，改它会抛 UnsupportedOperationException")
    void listOfIsImmutable() {
        List<String> list = List.of("a", "b", "c");

        // 试着改一下——这里必须抛异常，否则说明你用的根本不是不可变集合
        assertThatThrownBy(() -> list.add("d"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.remove(0))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.set(0, "x"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("List.of 不允许 null 元素，创建时就抛 NPE（而不是用的时候才报错）")
    void listOfRejectsNull() {
        assertThatThrownBy(() -> List.of("a", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Arrays.asList 只是'固定长度'，不是不可变：能 set，不能 add/remove")
    void arraysAsListIsFixedSizeNotImmutable() {
        List<String> list = Arrays.asList("a", "b");

        list.set(0, "x");                       // ✅ 允许，因为没改变长度
        assertThat(list).containsExactly("x", "b");

        assertThatThrownBy(() -> list.add("c")) // ❌ 长度变了就不行
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Collections.unmodifiableList 只是个'只读视图'，原集合变了它也会变")
    void unmodifiableListIsJustAView() {
        List<String> source = new ArrayList<>(List.of("a"));
        List<String> view = Collections.unmodifiableList(source);

        source.add("b");                        // 改原集合
        assertThat(view).hasSize(2);            // 视图跟着变——因为它和 source 共用同一份数据
        assertThatThrownBy(() -> view.add("c"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("List.copyOf 是真拷贝，原集合再变也影响不到它")
    void copyOfIsARealCopy() {
        List<String> source = new ArrayList<>(List.of("a"));
        List<String> copy = List.copyOf(source);

        source.add("b");
        assertThat(copy).hasSize(1);            // 不受影响
    }

    @Test
    @DisplayName("Map.of 也是不可变的，而且最多只能写 10 组键值对")
    void mapOfIsImmutable() {
        Map<String, Integer> map = Map.of("a", 1, "b", 2);

        assertThatThrownBy(() -> map.put("c", 3))
                .isInstanceOf(UnsupportedOperationException.class);

        // 超过 10 对就得用 Map.ofEntries 或 HashMap —— 这是 Map.of 的硬限制
        // Map.of("k1",1,"k2",2,...) 写到第 11 对编译就过不去
    }

    @Test
    @DisplayName("不可变集合适合暴露给外部，别把内部可变集合直接 return 出去")
    void defensiveCopyPattern() {
        // 这就是"防御性拷贝"：类内部保存可变集合，对外只给不可变视图
        class Order {
            private final List<String> items = new ArrayList<>();

            void add(String item) { items.add(item); }

            List<String> getItems() {
                return List.copyOf(items);      // ⬅️ 关键：不给外面改的机会
            }
        }

        Order order = new Order();
        order.add("苹果");
        List<String> snapshot = order.getItems();

        assertThatThrownBy(() -> snapshot.add("香蕉"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(order.getItems()).containsExactly("苹果");
    }
}
