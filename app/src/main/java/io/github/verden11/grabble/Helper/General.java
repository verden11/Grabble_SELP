package io.github.verden11.grabble.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.verden11.grabble.R;

/**
 * General static helpers
 */

public class General {

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    public static class WordScoreLine {

        private String prop1;
        private String prop2;

        public WordScoreLine(String prop1, String prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        public String getProp1() {
            return prop1;
        }

        public String getProp2() {
            return prop2;
        }
    }

    public static class CustomAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<WordScoreLine> objects;

        private class ViewHolder {
            TextView textView1;
            TextView textView2;
        }

        public CustomAdapter(Context context, ArrayList<WordScoreLine> objects) {
            inflater = LayoutInflater.from(context);
            this.objects = objects;
        }

        public int getCount() {
            return objects.size();
        }

        public WordScoreLine getItem(int position) {
            return objects.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.collected_word_line, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.tv_line_word);
                holder.textView2 = (TextView) convertView.findViewById(R.id.tv_line_score);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView1.setText(objects.get(position).getProp1());
            holder.textView2.setText(objects.get(position).getProp2());
            return convertView;
        }
    }


    public static int calculateWordValue(String word) {
        int score = 0;
        for (int i = 0; i < word.length(); i++) {
            score += Queries.getCharValue(word.charAt(i));
        }
        return score;
    }

}
