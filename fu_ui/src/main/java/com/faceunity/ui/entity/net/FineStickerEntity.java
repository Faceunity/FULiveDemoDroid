package com.faceunity.ui.entity.net;



import java.util.ArrayList;
/**
 * @author Qinyu on 2021-03-17
 * @description
 */
public class FineStickerEntity {
    /**
     * count : 2
     * docs : [{"_id":"6051a0aa6d670000230030c7","index":1,"tool":{"_id":"5f6ae24e570100009e006547","bundle":{"remark":"优化背景分割，提高边缘稳定性，需要SDK v6.2.0支持","uid":"0a176380-b33e-11e9-8e6a-35012f229fc1.bundle","size":3872.85,"name":"hez_ztt_fu.bundle"},"icon":{"remark":"v1.0","uid":"167f72e0-e173-11e8-b908-51298abcbcf5.png","size":61.83,"name":"hez_ztt_fu.png","url":"http://tools-manage.oss-cn-hangzhou.aliyuncs.com/167f72e0-e173-11e8-b908-51298abcbcf5.png?OSSAccessKeyId=LTAIZvD3ylHAD1vH&Expires=1616057194&Signature=4aesuhXnenASOXCQwl0logppBM4%3D"}},"platform":"mobile","tag":"中级道具"},{"_id":"6051a2d36d670000230030c8","index":2,"tool":{"_id":"5f6ae24e570100009e006547","bundle":{"remark":"优化背景分割，提高边缘稳定性，需要SDK v6.2.0支持","uid":"0a176380-b33e-11e9-8e6a-35012f229fc1.bundle","size":3872.85,"name":"hez_ztt_fu.bundle"},"icon":{"remark":"v1.0","uid":"167f72e0-e173-11e8-b908-51298abcbcf5.png","size":61.83,"name":"hez_ztt_fu.png","url":"http://tools-manage.oss-cn-hangzhou.aliyuncs.com/167f72e0-e173-11e8-b908-51298abcbcf5.png?OSSAccessKeyId=LTAIZvD3ylHAD1vH&Expires=1616057194&Signature=4aesuhXnenASOXCQwl0logppBM4%3D"}},"platform":"mobile","tag":"中级道具"}]
     */

    private int count;
    private ArrayList<DocsBean> docs;
    private boolean isOnline; //手动设置

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<DocsBean> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<DocsBean> docs) {
        this.docs = docs;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public static class DocsBean {
        /**
         * _id : 6051a0aa6d670000230030c7
         * index : 1
         * tool : {"_id":"5f6ae24e570100009e006547","bundle":{"remark":"优化背景分割，提高边缘稳定性，需要SDK v6.2.0支持","uid":"0a176380-b33e-11e9-8e6a-35012f229fc1.bundle","size":3872.85,"name":"hez_ztt_fu.bundle"},"icon":{"remark":"v1.0","uid":"167f72e0-e173-11e8-b908-51298abcbcf5.png","size":61.83,"name":"hez_ztt_fu.png","url":"http://tools-manage.oss-cn-hangzhou.aliyuncs.com/167f72e0-e173-11e8-b908-51298abcbcf5.png?OSSAccessKeyId=LTAIZvD3ylHAD1vH&Expires=1616057194&Signature=4aesuhXnenASOXCQwl0logppBM4%3D"}}
         * platform : mobile
         * tag : 中级道具
         */

        private String _id;
        private int index;
        private ToolBean tool;
        private String platform;
        private String tag;

        private String filePath; //手动设置
        private boolean isDownloading; //手动设置

//        public Effect cast2Effect() {
//            return new Effect(tool.bundle.name, 0, filePath, 1, Effect.EFFECT_TYPE_STICKER, 0);
//        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public ToolBean getTool() {
            return tool;
        }

        public void setTool(ToolBean tool) {
            this.tool = tool;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public boolean isDownloading() {
            return isDownloading;
        }

        public void setDownloading(boolean downloading) {
            isDownloading = downloading;
        }

        public static class ToolBean {
            /**
             * _id : 5f6ae24e570100009e006547
             * bundle : {"remark":"优化背景分割，提高边缘稳定性，需要SDK v6.2.0支持","uid":"0a176380-b33e-11e9-8e6a-35012f229fc1.bundle","size":3872.85,"name":"hez_ztt_fu.bundle"}
             * icon : {"remark":"v1.0","uid":"167f72e0-e173-11e8-b908-51298abcbcf5.png","size":61.83,"name":"hez_ztt_fu.png","url":"http://tools-manage.oss-cn-hangzhou.aliyuncs.com/167f72e0-e173-11e8-b908-51298abcbcf5.png?OSSAccessKeyId=LTAIZvD3ylHAD1vH&Expires=1616057194&Signature=4aesuhXnenASOXCQwl0logppBM4%3D"}
             */

            private String _id;
            private BundleBean bundle;
            private IconBean icon;
            private String adapter;

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

            public BundleBean getBundle() {
                return bundle;
            }

            public void setBundle(BundleBean bundle) {
                this.bundle = bundle;
            }

            public IconBean getIcon() {
                return icon;
            }

            public void setIcon(IconBean icon) {
                this.icon = icon;
            }

            public String getAdapter() {
                return adapter;
            }

            public void setAdapter(String adapter) {
                this.adapter = adapter;
            }

            public static class BundleBean {
                /**
                 * remark : 优化背景分割，提高边缘稳定性，需要SDK v6.2.0支持
                 * uid : 0a176380-b33e-11e9-8e6a-35012f229fc1.bundle
                 * size : 3872.85
                 * name : hez_ztt_fu.bundle
                 */

                private String remark;
                private String uid;
                private double size;
                private String name;

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getUid() {
                    return uid;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public double getSize() {
                    return size;
                }

                public void setSize(double size) {
                    this.size = size;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            public static class IconBean {
                /**
                 * remark : v1.0
                 * uid : 167f72e0-e173-11e8-b908-51298abcbcf5.png
                 * size : 61.83
                 * name : hez_ztt_fu.png
                 * url : http://tools-manage.oss-cn-hangzhou.aliyuncs.com/167f72e0-e173-11e8-b908-51298abcbcf5.png?OSSAccessKeyId=LTAIZvD3ylHAD1vH&Expires=1616057194&Signature=4aesuhXnenASOXCQwl0logppBM4%3D
                 */

                private String remark;
                private String uid;
                private double size;
                private String name;
                private String url;

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getUid() {
                    return uid;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public double getSize() {
                    return size;
                }

                public void setSize(double size) {
                    this.size = size;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
