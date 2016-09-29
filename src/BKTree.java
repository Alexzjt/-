import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * BK����������������ƴд�����ѯ
 * 
 * 1.�����ռ䡣
 * ��������ռ���������������
 * d(x,y) = 0 <-> x = y (����x��y�ľ���Ϊ0����x=y)
 * d(x,y) = d(y,x) (x��y�ľ����ͬ��y��x�ľ���)
 * d(x,y) + d(y,z) >= d(x,z)  �����ǲ���ʽ��
 * 
 * 2���༭���루 Levenshtein Distance�����ϻ�����������������Ķ����ռ�
 * 
 * 3����Ҫ��һ�����ۣ�������������������������query��ʾ�����������ַ��������ַ���Ϊ������
 *    nΪ�����ҵ��ַ�����query�����뷶Χ�����ǿ�����һ���ַ���A����query���бȽϣ���
 *    �����Ϊd���������ǲ���ʽ�ǳ����ģ���������query������n��Χ�ڵ���һ���ַ�תB��
 *    ������A�ľ������Ϊd+n����СΪd-n��
 *    
 *    �������£�
 *    d(query, B) + d(B, A) >= d(query, A), �� d(query, B) + d(A,B) >= d -->  d(A,B) >= d - d(query, B) >= d - n
 *    d(A, B) <= d(A,query) + d(query, B), �� d(query, B) <= d + d(query, B) <= d + n
 *        ��ʵ�������Եõ�  d(query, A) + d(A,B) >= d(query, B)  
 *                 -->   d(A,B) >= d(query, B) - d(query, A)  
 *                 -->  d(A,B) >= 1 - d >= 0 (query��B����)  ���� A��B����ͬһ���ַ���d(A,B)>=1
 *    ���ԣ�   min{1, d - n} <= d(A,B) <= d + n
 *    
 *    ������һ�ص㣬BK����ʵ��ʱ���ӽڵ㵽���ڵ��ȨֵΪ�ӽڵ㵽���ڵ�ľ��루��Ϊd1����
 *    ������һ��Ԫ�ص�����Ԫ�أ�����Ԫ���븸�ڵ�ľ��룬��Ϊd, ���ӽڵ���������Ҫ���
 *    ����Ԫ�أ��϶���Ȩֵ��d - n <= d1 <= d + n��Χ�ڣ���Ȼ�ˣ��ڷ�Χ�ڣ������Ԫ�صľ���Ҳδ��һ������Ҫ��
 *    ���൱���ڲ���ʱ�����˼�֦��Ȼ����Ҫ�������������������������Ϊ1��Χ�Ĳ�ѯ���������벻�ᳬ������5-8%��
 *    ���Ҿ���Ϊ2�Ĳ�ѯ���������벻�ᳬ������17-25%��

 * �μ���
 * http://blog.notdot.net/2007/4/Damn-Cool-Algorithms-Part-1-BK-Trees��ԭ�ģ�
 * @author yifeng
 *
 */
public class BKTree<T>{
    private final MetricSpace<T> metricSpace;
    
    private Node<T> root;
    
    public BKTree(MetricSpace<T> metricSpace) {
        this.metricSpace = metricSpace;
    }
    
    /**
     * ����ĳһ������Ԫ�ش���BK��
     * 
     * @param ms
     * @param elems
     * @return
     */
    public static <E> BKTree<E> mkBKTree(MetricSpace<E> ms, Collection<E> elems) {
        
        BKTree<E> bkTree = new BKTree<E>(ms);
        
        for (E elem : elems) {
            bkTree.put(elem);
        }
        
        return bkTree;
    }

    /**
     * BK�������Ԫ��
     * 
     * @param term
     */
    public void put(T term) {
        if (root == null) {
            root = new Node<T>(term);
        } else {
            root.add(metricSpace, term);
        }
    }
    
    /**
     * ��ѯ����Ԫ��
     * 
     * @param term
     *         ����ѯ��Ԫ��
     * @param radius
     *         ���Ƶľ��뷶Χ
     * @return
     *         ������뷶Χ������Ԫ��
     */
    public Set<T> query(T term, double radius) {
        
        Set<T> results = new HashSet<T>();
        
        if (root != null) {
            root.query(metricSpace, term, radius, results);
        }
        
        return results;
    }
    
    private static final class Node<T> {
    
        private final T value;
        
        /**
         *  ��һ��map�洢�ӽڵ�
         */
        private final Map<Double, Node<T>> children;
        
        public Node(T term) {
            this.value = term;
            this.children = new HashMap<Double, BKTree.Node<T>>();
        }
        
        public void add(MetricSpace<T> ms, T value) {
            // value�븸�ڵ�ľ���
            Double distance = ms.distance(this.value, value);
            
            // ����Ϊ0����ʾԪ����ͬ������
            if (distance == 0) {
                return;
            }
            
            // �Ӹ��ڵ���ӽڵ��в���child���������Ϊdistance
            Node<T> child = children.get(distance);
            
            
            if (child == null) {
                // �����븸�ڵ�Ϊdistance���ӽڵ㲻���ڣ���ֱ�����һ���µ��ӽڵ�
                children.put(distance, new Node<T>(value));
            } else {
                // �����븸�ڵ�Ϊdistance�ӽڵ���ڣ���ݹ�Ľ�value��ӵ����ӽڵ���
                child.add(ms, value);
            }
        }
        
        public void query(MetricSpace<T> ms, T term, double radius, Set<T> results) {
            
            double distance = ms.distance(this.value, term);
            
            // �븸�ڵ�ľ���С����ֵ������ӵ�������У�����������Ѱ��
            if (distance <= radius) {
                results.add(this.value);
            }
            
            // �ӽڵ�ľ�������С�����������֮��ġ�
            // �ɶ����ռ��d(x,y) + d(y,z) >= d(x,z)��һ�����в��ҵ�value���ӽڵ�ľ��뷶Χ���£�
            // min = {1,distance -radius}, max = distance + radius
            for (double i = Math.max(distance - radius, 1); i <= distance + radius; ++i) {
                
                Node<T> child = children.get(i);
                
                // �ݹ����
                if (child != null) {
                    child.query(ms, term, radius, results);
                }
                
            }    
        }
    }
}