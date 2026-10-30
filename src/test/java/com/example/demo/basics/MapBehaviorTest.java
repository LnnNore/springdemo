package com.example.demo.basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * S3-3：Map 与 hashCode 契约。这一组最接近面试现场。
 *
 * <p>运行方式：{@code mvn test -Dtest=MapBehaviorTest}
 */
class MapBehaviorTest {

    @Test
    @DisplayName("三种 Map 的顺序特性：HashMap 不保证、LinkedHashMap 保持插入序、TreeMap 按 key 排序")
    void iterationOrderDiffers() {
        Map<String, Integer> hash = new HashMap<>();
        Map<String, Integer> linked = new LinkedHashMap<>();
        Map<String, Integer> tree = new TreeMap<>();

        for (String k : new String[]{"c", "a", "b"}) {
            hash.put(k, 1);
            linked.put(k, 1);
            tree.put(k, 1);
        }

        // HashMap 不保证顺序 —— 所以只能用"包含这些元素"来断言，不能断言顺序
        assertThat(hash.keySet()).containsExactlyInAnyOrder("a", "b", "c");
        // LinkedHashMap 保持插入顺序
        assertThat(linked.keySet()).containsExactly("c", "a", "b");
        // TreeMap 按 key 自然排序
        assertThat(tree.keySet()).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("\"Aa\" 和 \"BB\" 的 hashCode 相同——这就是哈希冲突")
    void hashCollisionIsReal() {
        assertThat("Aa".hashCode()).isEqualTo("BB".hashCode());

        // 虽然冲突，但两个 key 依然都能正确存取：HashMap 用 equals 在同一个桶里区分它们
        Map<String, String> map = new HashMap<>();
        map.put("Aa", "第一个");
        map.put("BB", "第二个");

        assertThat(map).hasSize(2);
        assertThat(map.get("Aa")).isEqualTo("第一个");
        assertThat(map.get("BB")).isEqualTo("第二个");

        // 面试考点：hashCode 冲突不影响正确性（靠 equals 兜底），只影响性能（退化成链表/红黑树）
    }

    @Test
    @DisplayName("record 自动生成 equals/hashCode，所以 HashSet 能正确去重")
    void recordHasEqualsAndHashCode() {
        record Point(int x, int y) { }

        Set<Point> set = new java.util.HashSet<>();
        set.add(new Point(1, 2));
        set.add(new Point(1, 2));      // 内容相同 → 被当成重复

        assertThat(set).hasSize(1);
    }

    @Test
    @DisplayName("普通类不重写 equals/hashCode，内容相同也去不了重")
    void plainClassWithoutEqualsCannotDedupe() {
        // 故意不重写 equals/hashCode
        class BadPoint {
            final int x, y;
            BadPoint(int x, int y) { this.x = x; this.y = y; }
        }

        Set<BadPoint> set = new java.util.HashSet<>();
        set.add(new BadPoint(1, 2));
        set.add(new BadPoint(1, 2));

        // 两个"看起来一样"的对象都在里面，因为用的是 Object 的默认实现（比引用地址）
        assertThat(set).hasSize(2);

        // 结论：只要你的对象要放进 HashSet / 当 HashMap 的 key，就必须重写 equals + hashCode
        // ⚠️ 而且两者必须一起重写，只写一个会出现"放进去就取不出来"的诡异 bug
    }

    @Test
    @DisplayName("HashMap 允许 null 键和 null 值，TreeMap 不允许 null 键")
    void nullHandling() {
        Map<String, String> hash = new HashMap<>();
        hash.put(null, "键是 null");
        hash.put("k", null);

        assertThat(hash.get(null)).isEqualTo("键是 null");
        assertThat(hash).containsKey("k");

        Map<String, String> tree = new TreeMap<>();
        assertThatThrownBy(() -> tree.put(null, "x"))   // 排序需要比较，null 比不了
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("get 一个不存在的 key 返回 null，不会抛异常（和 List.get 越界不同）")
    void getMissingKeyReturnsNull() {
        Map<String, Integer> map = new HashMap<>();
        assertThat(map.get("不存在")).isNull();
        // 这也是为什么推荐 getOrDefault
        assertThat(map.getOrDefault("不存在", 0)).isZero();
    }
}
