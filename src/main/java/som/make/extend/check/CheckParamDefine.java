package som.make.extend.check;

import java.util.ArrayList;
import java.util.List;

public class CheckParamDefine {

    private List<CheckGroup> notNull;
    private List<CheckGroup> notAllNull;

    public List<CheckGroup> getNotNull() {
        return notNull;
    }

    public void setNotNull(List<CheckGroup> notNull) {
        this.notNull = notNull;
    }

    public List<CheckGroup> getNotAllNull() {
        return notAllNull;
    }

    public void setNotAllNull(List<CheckGroup> notAllNull) {
        this.notAllNull = notAllNull;
    }

    public class CheckGroup {
        List<CheckSingle> checkSingleList = new ArrayList<>();

        public List<CheckSingle> getCheckSingleList() {
            return checkSingleList;
        }

        public void addCheckSingle(CheckSingle checkSingle) {
            checkSingleList.add(checkSingle);
        }

    }

    public class CheckSingle {
        private String name;
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public void parseAnnotation(CheckParam checkParam) {
        // 解析notNull
        notNull = parseAnnotationProperty(checkParam.notNull());
        // 解析notAllNull
        notAllNull = parseAnnotationProperty(checkParam.notAllNull());
    }

    /**
     * 将注解参数检测中的属性解析成bean
     * @param properties 属性
     * @return 返回解析好的bean
     */
    private List<CheckGroup> parseAnnotationProperty(String[] properties) {
        if (properties != null && properties.length > 0) {
            List<CheckGroup> checkGroupList = new ArrayList<>();
            // 这是第一层解析的属性
            for (String property: properties) {
                CheckGroup cg = new CheckGroup();
                String[] sa = property.split("&&");
                // 解析&&符号，&&符号将多个属性放在一起
                for (String s: sa) {
                    CheckSingle cs = new CheckSingle();
                    // -前面是属性的code，后面是错误信息
                    if (s.contains("-")) {
                        String[] single = s.split("-");
                        cs.setName(single[0]);
                        cs.setMessage(single[1]);
                    } else {
                        cs.setName(s);
                        cs.setMessage("请求中的参数不符合要求，请检查。");
                    }
                    cg.addCheckSingle(cs);
                }
                checkGroupList.add(cg);
            }
            return checkGroupList;
        }
        return null;
    }

}
