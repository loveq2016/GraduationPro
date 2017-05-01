package org.ql.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;

import org.ql.utils.debug.QLLog;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class QLFileUtil {
	
	public static boolean createFile(String filepath, boolean recursion) throws IOException {
		return createFile(new File(filepath),recursion);
	}
	
	public static boolean createFile(File file, boolean recursion) throws IOException {
		boolean result = false;
		if (!file.exists()) {
			try {
				result = file.createNewFile();
			} catch (IOException e) {
				if (!recursion) {
					throw e;
				}
				File parent = file.getParentFile();
				if (!parent.exists())
					parent.mkdirs();
				try {
					result = file.createNewFile();
				} catch (IOException e1) {
					throw e1;
				}
			}
		}
		return result;
	}

	public static String FormetFileSize(long fileSize) {
		DecimalFormat df = new DecimalFormat("0.0");
		String fileSizeString = "";
		if (fileSize < 1024L)
			fileSizeString = df.format(fileSize) + "B";
		else if (fileSize < 1048576L)
			fileSizeString = df.format(fileSize / 1024.0D) + "K";
		else if (fileSize < 1073741824L)
			fileSizeString = df.format(fileSize / 1048576.0D) + "M";
		else {
			fileSizeString = df.format(fileSize / 1073741824.0D) + "G";
		}
		return fileSizeString;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// http://www.fileinfo.com/filetypes/video , "dat" , "bin" , "rms"
	public static final String[] VIDEO_EXTENSIONS = { "264", "3g2", "3gp", "3gp2", "3gpp", "3gpp2", "3mm", "3p2", "60d", "aep", "ajp", "amv", "amx", "arf", "asf", "asx", "avb", "avd", "avi", "avs", "avs", "axm", "bdm", "bdmv", "bik", "bix", "bmk", "box", "bs4", "bsf", "byu", "camre", "clpi", "cpi", "cvc", "d2v", "d3v", "dav", "dce", "dck", "ddat", "dif", "dir", "divx", "dlx", "dmb", "dmsm", "dmss", "dnc", "dpg", "dream", "dsy", "dv", "dv-avi", "dv4", "dvdmedia", "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt", "evo", "eye", "f4p", "f4v", "fbr", "fbr", "fbz", "fcp", "flc", "flh", "fli", "flv", "flx", "gl", "grasp", "gts", "gvi", "gvp", "hdmov", "hkm", "ifo", "imovi", "imovi", "iva", "ivf", "ivr", "ivs", "izz", "izzy", "jts", "lsf", "lsx", "m15", "m1pg", "m1v", "m21", "m21", "m2a", "m2p", "m2t", "m2ts", "m2v", "m4e", "m4u", "m4v", "m75", "meta", "mgv", "mj2", "mjp", "mjpg", "mkv", "mmv", "mnv", "mod", "modd", "moff", "moi", "moov", "mov", "movie", "mp21", "mp21", "mp2v", "mp4", "mp4v", "mpe", "mpeg", "mpeg4", "mpf", "mpg", "mpg2", "mpgin", "mpl", "mpls", "mpv", "mpv2", "mqv", "msdvd", "msh", "mswmm", "mts", "mtv", "mvb", "mvc", "mvd", "mve", "mvp", "mxf", "mys", "ncor", "nsv", "nvc", "ogm", "ogv", "ogx", "osp", "par", "pds", "pgi", "piv", "playlist", "pmf", "prel", "pro", "prproj", "psh", "pva", "pvr", "pxv", "qt", "qtch", "qtl", "qtm", "qtz", "rcproject", "rdb", "rec", "rm", "rmd", "rmp", "rmvb", "roq", "rp", "rts", "rts", "rum", "rv", "sbk", "sbt", "scm", "scm", "scn", "sec", "seq", "sfvidcap", "smil", "smk", "sml", "smv", "spl", "ssm", "str", "stx", "svi", "swf", "swi", "swt", "tda3mt", "tivo", "tix", "tod", "tp", "tp0", "tpd", "tpr", "trp", "ts", "tvs", "vc1", "vcr", "vcv", "vdo", "vdr", "veg", "vem", "vf", "vfw", "vfz", "vgz", "vid", "viewlet", "viv", "vivo", "vlab", "vob", "vp3", "vp6", "vp7", "vpj", "vro", "vsp", "w32", "wcp", "webm", "wm", "wmd", "wmmp", "wmv", "wmx", "wp3", "wpl", "wtv", "wvx", "xfl", "xvid", "yuv", "zm1", "zm2", "zm3", "zmv" };
	// http://www.fileinfo.com/filetypes/audio , "spx" , "mid" , "sf"
	public static final String[] AUDIO_EXTENSIONS = { "4mp", "669", "6cm", "8cm", "8med", "8svx", "a2m", "aa", "aa3", "aac", "aax", "abc", "abm", "ac3", "acd", "acd-bak", "acd-zip", "acm", "act", "adg", "afc", "agm", "ahx", "aif", "aifc", "aiff", "ais", "akp", "al", "alaw", "all", "amf", "amr", "ams", "ams", "aob", "ape", "apf", "apl", "ase", "at3", "atrac", "au", "aud", "aup", "avr", "awb", "band", "bap", "bdd", "box", "bun", "bwf", "c01", "caf", "cda", "cdda", "cdr", "cel", "cfa", "cidb", "cmf", "copy", "cpr", "cpt", "csh", "cwp", "d00", "d01", "dcf", "dcm", "dct", "ddt", "dewf", "df2", "dfc", "dig", "dig", "dls", "dm", "dmf", "dmsa", "dmse", "drg", "dsf", "dsm", "dsp", "dss", "dtm", "dts", "dtshd", "dvf", "dwd", "ear", "efa", "efe", "efk", "efq", "efs", "efv", "emd", "emp", "emx", "esps", "f2r", "f32", "f3r", "f4a", "f64", "far", "fff", "flac", "flp", "fls", "frg", "fsm", "fzb", "fzf", "fzv", "g721", "g723", "g726", "gig", "gp5", "gpk", "gsm", "gsm", "h0", "hdp", "hma", "hsb", "ics", "iff", "imf", "imp", "ins", "ins", "it", "iti", "its", "jam", "k25", "k26", "kar", "kin", "kit", "kmp", "koz", "koz", "kpl", "krz", "ksc", "ksf", "kt2", "kt3", "ktp", "l", "la", "lqt", "lso", "lvp", "lwv", "m1a", "m3u", "m4a", "m4b", "m4p", "m4r", "ma1", "mdl", "med", "mgv", "midi", "miniusf", "mka", "mlp", "mmf", "mmm", "mmp", "mo3", "mod", "mp1", "mp2", "mp3", "mpa", "mpc", "mpga", "mpu", "mp_", "mscx", "mscz", "msv", "mt2", "mt9", "mte", "mti", "mtm", "mtp", "mts", "mus", "mws", "mxl", "mzp", "nap", "nki", "nra", "nrt", "nsa", "nsf", "nst", "ntn", "nvf", "nwc", "odm", "oga", "ogg", "okt", "oma", "omf", "omg", "omx", "ots", "ove", "ovw", "pac", "pat", "pbf", "pca", "pcast", "pcg", "pcm", "peak", "phy", "pk", "pla", "pls", "pna", "ppc", "ppcx", "prg", "prg", "psf", "psm", "ptf", "ptm", "pts", "pvc", "qcp", "r", "r1m", "ra", "ram", "raw", "rax", "rbs", "rcy", "rex", "rfl", "rmf", "rmi", "rmj", "rmm", "rmx", "rng", "rns", "rol", "rsn", "rso", "rti", "rtm", "rts", "rvx", "rx2", "s3i", "s3m", "s3z", "saf", "sam", "sb", "sbg", "sbi", "sbk", "sc2", "sd", "sd", "sd2", "sd2f", "sdat", "sdii", "sds", "sdt", "sdx", "seg", "seq", "ses", "sf2", "sfk", "sfl", "shn", "sib", "sid", "sid", "smf", "smp", "snd", "snd", "snd", "sng", "sng", "sou", "sppack", "sprg", "sseq", "sseq", "ssnd", "stm", "stx", "sty", "svx", "sw", "swa", "syh", "syw", "syx", "td0", "tfmx", "thx", "toc", "tsp", "txw", "u", "ub", "ulaw", "ult", "ulw", "uni", "usf", "usflib", "uw", "uwf", "vag", "val", "vc3", "vmd", "vmf", "vmf", "voc", "voi", "vox", "vpm", "vqf", "vrf", "vyf", "w01", "wav", "wav", "wave", "wax", "wfb", "wfd", "wfp", "wma", "wow", "wpk", "wproj", "wrk", "wus", "wut", "wv", "wvc", "wve", "wwu", "xa", "xa", "xfs", "xi", "xm", "xmf", "xmi", "xmz", "xp", "xrns", "xsb", "xspf", "xt", "xwb", "ym", "zvd", "zvr" };

	private static final HashSet<String> mHashVideo;
	private static final HashSet<String> mHashAudio;
	private static final double KB = 1024.0;
	private static final double MB = KB * KB;
	private static final double GB = KB * KB * KB;

	static {
		mHashVideo = new HashSet<String>(Arrays.asList(VIDEO_EXTENSIONS));
		mHashAudio = new HashSet<String>(Arrays.asList(AUDIO_EXTENSIONS));
	}

	/** 是否是音频或者视频 */
	public static boolean isVideoOrAudio(File f) {
		final String ext = getFileExtension(f);
		return mHashVideo.contains(ext) || mHashAudio.contains(ext);
	}

	public static boolean isVideoOrAudio(String f) {
		final String ext = getUrlExtension(f);
		return mHashVideo.contains(ext) || mHashAudio.contains(ext);
	}

	public static boolean isVideo(File f) {
		final String ext = getFileExtension(f);
		return mHashVideo.contains(ext);
	}

	/** 获取文件后缀 */
	public static String getFileExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	public static String getUrlFileName(String url) {
		int slashIndex = url.lastIndexOf('/');
		int dotIndex = url.lastIndexOf('.');
		String filenameWithoutExtension;
		if (dotIndex == -1) {
			filenameWithoutExtension = url.substring(slashIndex + 1);
		} else {
			filenameWithoutExtension = url.substring(slashIndex + 1, dotIndex);
		}
		return filenameWithoutExtension;
	}

	/**扩展名*/
	public static String getUrlExtension(String url) {
		if (!QLStringUtils.isEmpty(url)) {
			int i = url.lastIndexOf('.');
			if (i > 0 && i < url.length() - 1) {
				return url.substring(i + 1).toLowerCase(Locale.getDefault());
			}
		}
		return "";
	}
	
	public static String getUrlDownExtension(String url) {
		String result = null;
		if (!QLStringUtils.isEmpty(url)) {
			StringTokenizer st = new StringTokenizer(url, "/");
			while (st.hasMoreTokens()) {
				url = st.nextToken();
			}
			if (!QLStringUtils.isEmpty(url)) {
				int i = url.indexOf('.');
				if (i > 0 && i < url.length() - 1){ 
					result =  url.substring(i+1);
					i = result.indexOf('?');
					if (i > 0 && i < result.length() - 1) 
						result =  result.substring(0,i);
				}
			}
		}
		return result;
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static String showFileSize(long size) {
		String fileSize;
		if (size < KB)
			fileSize = size + "B";
		else if (size < MB)
			fileSize = String.format("%.1f", size / KB) + "K";
		else if (size < GB)
			fileSize = String.format("%.1f", size / MB) + "MB";
		else
			fileSize = String.format("%.1f", size / GB) + "GB";

		return fileSize;
	}
	
	public static String getFromAssets(Context context, String fileName, boolean addEnterLindEnd) {
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null){		
				if(addEnterLindEnd){
					Result += line+"\n";
				}else{					
					Result += line;
				}
			}
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static final StatFs mStatFs = new StatFs(Environment.getDataDirectory().getPath());
	/**
	 * @return 手机内存总容量
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		mStatFs.restat(path.getPath());
		long blockSize = mStatFs.getBlockSize();
		long totalBlocks = mStatFs.getBlockCount();
		return totalBlocks * blockSize;
	}
	
	/**
	 * @return 手机内存可用容量
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		mStatFs.restat(path.getPath());
		long blockSize = mStatFs.getBlockSize();
		long availableBlocks = mStatFs.getAvailableBlocks();
		return availableBlocks * blockSize;
	}
	
	/**
	 * 否有外部存储设备
	 * @return
	 */
	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * @return SD卡总容量
	 */
	public static long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			mStatFs.restat(path.getPath());
			long blockSize = mStatFs.getBlockSize();
			long totalBlocks = mStatFs.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return -1;
		}
	}
	
	/**
	 * @return SD卡可用容量
	 */
	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			mStatFs.restat(path.getPath());
			long blockSize = mStatFs.getBlockSize();
			long availableBlocks = mStatFs.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return -1;
		}
	}
	
	public static String getParentPath(final String path)
	{
		if(path==null)
			return null;
		String parenPath=null;
		int idx=path.lastIndexOf("/");
		if(idx!=-1&&idx!=0)
			parenPath=path.substring(0, idx);
		return parenPath;
	}
	
	public static boolean isExists(String fileName){
		File file = new File(fileName);
		return file.exists();
	}
	
	public static boolean writeStringToFile(String fileName,String content)
	{
		File file = new File(fileName);
		if(content==null)
			return false;
		
		if(!file.exists()){
			String filepath=file.getAbsolutePath();
			String path=getParentPath(filepath);
			File dir=new File(path);
			dir.mkdirs();
		}
		FileOutputStream fos = null;
		byte[] data;
		try {
			fos=new FileOutputStream(file,false);
			data=content.getBytes();
			fos.write(data, 0, data.length);
			fos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null!=fos){					
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			data=null;
			fos=null;
		}
		return false;
	}
	
	public static String readStringFromFile(String fileName, boolean addEnterLindEnd) {
		StringBuilder result = new StringBuilder();
		FileInputStream fip = null;
		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		try {
			File file = new File(fileName);
			if(file.exists()&&file.isFile()){				
				fip = new FileInputStream(file);
				inputReader = new InputStreamReader(fip);
				bufReader = new BufferedReader(inputReader);
				String line = "";
				while ((line = bufReader.readLine()) != null){		
					if(addEnterLindEnd){
						result.append(line+"\n");
					}else{					
						result.append(line);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null!=bufReader){
					bufReader.close();					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(null!=inputReader){					
					inputReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(null!=fip){					
					fip.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
}