package mapp.com.sg.splite.CCAViewerUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import mapp.com.sg.splite.CCAViewerBackend.CCAComponent;

import mapp.com.sg.splite.CCAViewerBackend.CCARecord;
import mapp.com.sg.splite.R;

import static mapp.com.sg.splite.R.id.ccalabel;

/**
 * Created by Wee Kiat on 12/12/2017.
 */

public class CCAPointListAdapter extends BaseAdapter {

    private Context context;
    private CCARecord cca;

    class ComponentCtrlHolder{
        public TextView CCALabel;
        public ImageButton detail_ib;
        public TextView CCAPoint_tv;
        public ProgressBar CCA_pb;

        public ComponentCtrlHolder(TextView CCALabel, ImageButton detail_ib, TextView CCAPoint_tv, ProgressBar CCA_pb) {
            this.CCALabel = CCALabel;
            this.detail_ib = detail_ib;
            this.CCAPoint_tv = CCAPoint_tv;
            this.CCA_pb = CCA_pb;
        }
    }

    public CCAPointListAdapter(Context context, CCARecord cca) {
        this.context = context;
        this.cca = cca;
    }

    @Override
    public int getCount() {
        return cca.getCCAComponents().size();
    }

    @Override
    public CCAComponent getItem(int position) {
        return cca.getCCAComponents().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ComponentCtrlHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_cca_chart_item, null);

            holder = new ComponentCtrlHolder(
                    (TextView)view.findViewById(ccalabel),
                    (ImageButton)view.findViewById(R.id.detail_imageButton),
                    (TextView) view.findViewById(R.id.ccapoints),
                    (ProgressBar)view.findViewById(R.id.cca_progressBar)
            );
            view.setTag(holder);
        }else{
            holder = (ComponentCtrlHolder)view.getTag();
        }
        holder.detail_ib.setTag(position);

        int ccapoints = cca.getCCAComponents().get(position).getTotalpoint();
        holder.CCAPoint_tv.setText(Integer.toString(ccapoints) + " pts");
        holder.CCA_pb.setProgress(ccapoints > 10 ? 10 : ccapoints);

        holder.CCALabel.setText(cca.getCCAComponents().get(position).getCapitalisedName());

        return view;
    }
}
