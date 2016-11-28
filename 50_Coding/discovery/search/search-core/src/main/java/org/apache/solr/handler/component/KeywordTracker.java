package org.apache.solr.handler.component;


import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 2/28/13
 * Time: 4:15 PM
 */
public class KeywordTracker implements Runnable {
    final Logger logger = LoggerFactory.getLogger(KeywordTracker.class);
    public static final String LINE_SEPARATOR = "|^";
    private final static SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyyMMdd");
    private LinkedBlockingQueue<String> que;

    private String path;

    public KeywordTracker(String path) {
        this.path = path;
        que = new LinkedBlockingQueue<String>(20000);
    }

    public void saveKeyword(String keyword, String category, long foundNum) {
        if (keyword != null && !"".equals(keyword)) {
            try {
                StringBuilder line = new StringBuilder();
                line.append(keyword);
                line.append(LINE_SEPARATOR);
                line.append(category);
                line.append(LINE_SEPARATOR);
                line.append(foundNum);

                this.track(line.toString());
            } catch (Exception e) {
                logger.error("Something wrong happened when saving keyword :" + keyword);
            }
        }
    }


    private void track(String hehavior) {
        synchronized (que) {
            boolean b = que.offer(hehavior);
            if (!b) {
                logger.error("The queue of the TrackBehavior is full while trying to offer " + hehavior);
            }
        }
    }


    @Override
    public void run() {
        try {
            System.out.println("hello, I'm running! date= " + new Date() + "  -----" + this.path);
            String track = que.poll();
            if (track != null) {
                List<String> tracks = new ArrayList<String>();

                synchronized (que) {
                    tracks.add(track);
                    tracks.addAll(que);
                    que.clear();
                }

                putItToFile(tracks);
            }
            Thread.sleep(1000 * 60 * 3);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void putItToFile(List<String> tracks) {
        OutputStream os = null;
        BufferedOutputStream bos = null;

        try {
            if (tracks.size() > 0) {
                String fileName = getFileName(dayDateFormat.format(new Date()));
                os = new FileOutputStream(fileName, true);
                bos = new BufferedOutputStream(os);

                for (String t : tracks) {
                    bos.write(t.getBytes(Charsets.UTF_8));
                    bos.write("\n".getBytes(Charsets.UTF_8));
                }

                bos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public List<KeywordStat> getKeywordStat(String date) throws Exception {
        System.out.println("keywordStat begin ... ");
        List<KeywordStat> stats = new ArrayList<KeywordStat>();

        InputStream in = null;
        InputStreamReader fr = null;
        BufferedReader br = null;
        try {
            String fileName = this.getFileName(date);

            File file = new File(fileName);
            if (!file.exists()) {
                return stats;
            }

            in = new FileInputStream(file);
            fr = new InputStreamReader(in, Charsets.UTF_8);

            br = new BufferedReader(fr);
            String line = br.readLine();

            Multiset<String> wordsMultiset = HashMultiset.create();
            Map<String, Integer> countMap = new HashMap<String, Integer>();
            while (line != null) {
                try {

                    List<String> item = Lists.newArrayList(Splitter.on(LINE_SEPARATOR).split(line));
                    if (item != null && item.size() >= 3) {
                        String key = item.get(0) + LINE_SEPARATOR + item.get(1);
                        wordsMultiset.add(key);
                        countMap.put(key, Integer.valueOf(item.get(2)));
                    }

                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            for (Multiset.Entry en : wordsMultiset.entrySet()) {
                List<String> kc = Lists.newArrayList(Splitter.on(LINE_SEPARATOR).split((String) en.getElement()));
                if (kc != null && kc.size() >= 2) {
                    KeywordStat ks = new KeywordStat();
                    ks.setKeyword(kc.get(0));
                    ks.setCategory(kc.get(1));
                    ks.setCount(en.getCount());
                    String key = (String) en.getElement();
                    int num = countMap.get(key);
                    ks.setFoundNum(num);
                    ks.setDate(date);

                    stats.add(ks);
                }
            }

        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("keywordStat end ");
        return stats;
    }

    private String getFileName(String date) {
        String path = this.path + "behavior" + File.separatorChar;
        String fileName = path + "tracks_" + date + ".log";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return fileName;
    }
}
