package cs.unitec.steve.a7424.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cs.unitec.steve.a7424.R;
import cs.unitec.steve.a7424.models.Difficulty;
import cs.unitec.steve.a7424.models.Tournament;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.TournamentHolder> {

    private List<Tournament> data = new ArrayList<>();
    private boolean canEdit;
    private OnAnswerListener onAnswer;
    private OnEditListener onEdit;

    public TournamentAdapter(List<Tournament> data, boolean canEdit, OnAnswerListener onAnswer, OnEditListener onEdit) {
        this.data = (data != null) ? data : new ArrayList<>();
        this.canEdit = canEdit;
        this.onAnswer = onAnswer;
        this.onEdit = onEdit;
    }

    public void update(List<Tournament> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public TournamentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_tournament, parent, false);
        return new TournamentHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(TournamentHolder holder, int position) {
        Tournament d = data.get(position);
        if (d.getDifficulty() == Difficulty.EASY) {
            holder.easySign.setVisibility(View.VISIBLE);
            holder.mediumSign.setVisibility(View.GONE);
            holder.hardSign.setVisibility(View.GONE);
        }
        if (d.getDifficulty() == Difficulty.MEDIUM) {
            holder.mediumSign.setVisibility(View.VISIBLE);
            holder.easySign.setVisibility(View.GONE);
            holder.hardSign.setVisibility(View.GONE);
        }
        if (d.getDifficulty() == Difficulty.HARD) {
            holder.hardSign.setVisibility(View.VISIBLE);
            holder.easySign.setVisibility(View.GONE);
            holder.mediumSign.setVisibility(View.GONE);
        }
        holder.nameTxt.setText(d.getName());
        holder.cateTxt.setText(d.getCategory().getText());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateStr = format.format(d.getStartDate());
        String endDateStr = format.format(d.getEndDate());
        boolean isPast = new Date().after(d.getEndDate());
        boolean isComing = new Date().before(d.getStartDate());
        boolean isOnGoing = !isPast && !isComing;

        String stateStr = "";
        if (isPast) {
            stateStr = "Past";
        }
        if (isOnGoing) {
            stateStr = "On Going";
            holder.btnAnswer.setEnabled(true);
        }
        if (isComing) {
            stateStr = "Coming";
        }
        holder.dateRangeTxt.setText(String.format("[%s] %s - %s", stateStr, startDateStr, endDateStr));

        if (canEdit) {
            holder.btnEdit.setVisibility(View.VISIBLE);
        } else {
            holder.btnAnswer.setVisibility(View.VISIBLE);
        }
        holder.btnEdit.setOnClickListener(v -> onEdit.onEdit(d.getId()));
        holder.btnAnswer.setOnClickListener(v -> onAnswer.onAnswer(d.getId()));
    }

    public class TournamentHolder extends RecyclerView.ViewHolder {
        View view;
        TextView hardSign;
        TextView mediumSign;
        TextView easySign;
        TextView nameTxt;
        TextView dateRangeTxt;
        TextView cateTxt;
        Button btnEdit;
        Button btnAnswer;

        public TournamentHolder(View itemView) {
            super(itemView);
            view = itemView;
            hardSign = view.findViewById(R.id.tournament_item_hard_sign);
            mediumSign = view.findViewById(R.id.tournament_item_medium_sign);
            easySign = view.findViewById(R.id.tournament_item_easy_sign);
            nameTxt = view.findViewById(R.id.tournament_item_name);
            dateRangeTxt = view.findViewById(R.id.tournament_item_date_range);
            cateTxt = view.findViewById(R.id.tournament_item_cate);
            btnEdit = view.findViewById(R.id.tournament_item_edit_btn);
            btnAnswer = view.findViewById(R.id.tournament_item_answer_btn);
        }
    }

    public interface OnAnswerListener {
        void onAnswer(String id);
    }

    public interface OnEditListener {
        void onEdit(String id);
    }
}

