/**
 * �༭���룬 �ֳ�Levenshtein���룬��ָ�����ִ�֮�䣬��һ��ת����һ����������ٱ༭����������
 * ��������ɵı༭����������һ���ַ��滻����һ���ַ�������һ���ַ���ɾ��һ���ַ���
 * 
 * ʹ�ö�̬�滮�㷨���㷨���Ӷȣ�m*n��
 * 
 * @author yifeng
 *
 */
public class LevensteinDistance implements MetricSpace<String>{
    private double insertCost = 1;       // ����д�ɲ���ĺ�����������ϸ������
    private double deleteCost = 1;       // ����д��ɾ���ĺ�����������ϸ������
    private double substitudeCost = 1; // ����д���滻�ĺ�����������ϸ����������ʹ�ü��̾��롣
    
    public double computeDistance(String target,String source){
        int n = target.trim().length();
        int m = source.trim().length();
        
        double[][] distance = new double[n+1][m+1];
        
        distance[0][0] = 0;
        for(int i = 1; i <= m; i++){
            distance[0][i] = i;
        }
        for(int j = 1; j <= n; j++){
            distance[j][0] = j;
        }

        for(int i = 1; i <= n; i++){
            for(int j = 1; j <=m; j++){
                double min = distance[i-1][j] + insertCost;
                
                if(target.charAt(i-1) == source.charAt(j-1)){
                    if(min > distance[i-1][j-1]) 
                        min = distance[i-1][j-1];
                }else{
                    if(min > distance[i-1][j-1] + substitudeCost)
                        min = distance[i-1][j-1] + substitudeCost;
                }
                
                if(min > distance[i][j-1] + deleteCost){
                    min = distance[i][j-1] + deleteCost;
                }
                
                distance[i][j] = min;
            }
        }
        
        return distance[n][m];
    }

    @Override
    public double distance(String a, String b) {
        return computeDistance(a,b);
    }

    public static void main(String[] args) {
        LevensteinDistance distance = new LevensteinDistance();
        System.out.println(distance.computeDistance("���","����"));
    }
}