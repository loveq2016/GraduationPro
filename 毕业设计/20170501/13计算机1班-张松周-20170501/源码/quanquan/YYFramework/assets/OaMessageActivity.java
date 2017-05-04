public static long copyFile2(File srcFile, File destDir, String newFileName) {  
        long copySizes = 0;  
        if (!srcFile.exists()) {  
            System.out.println("源文件不存在");  
            copySizes = -1;  
        } else if (!destDir.exists()) {  
            System.out.println("目标目录不存在");  
            copySizes = -1;  
        } else if (newFileName == null) {  
            System.out.println("文件名为null");  
            copySizes = -1;  
        } else {  
            try {  
                FileChannel fcin = new FileInputStream(srcFile).getChannel();  
                FileChannel fcout = new FileOutputStream(new File(destDir,  
                        newFileName)).getChannel();  
                long size = fcin.size();  
                fcin.transferTo(0, fcin.size(), fcout);  
                fcin.close();  
                fcout.close();  
                copySizes = size;  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return copySizes;  
    }  