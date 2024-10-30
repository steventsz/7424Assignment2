package cs.unitec.steve.a7424.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cs.unitec.steve.a7424.R;
import cs.unitec.steve.a7424.models.Question;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerHolder> {

    private Context ctx;
    private List<Question> data = new ArrayList<>();
    private Callback callback;

    public interface Callback {
        void onAnswerSelected(int position, boolean isCorrect);
    }

    public AnswerAdapter(Context ctx, List<Question> data, Callback callback) {
        this.ctx = ctx;
        this.data = (data != null) ? data : new ArrayList<>();
        this.callback = callback;
    }

    public class AnswerHolder extends RecyclerView.ViewHolder {
        View view;
        TextView txtName;
        TextView answer;
        RadioGroup optionGroup;
        RadioButton optionTrue;
        RadioButton optionFalse;
        RadioButton optionNone;

        public AnswerHolder(View itemView) {
            super(itemView);
            view = itemView;
            txtName = view.findViewById(R.id.question_item_question);
            answer = view.findViewById(R.id.question_item_answer);
            optionGroup = view.findViewById(R.id.question_item_options);
            optionTrue = view.findViewById(R.id.question_item_option_true);
            optionFalse = view.findViewById(R.id.question_item_option_false);
            optionNone = view.findViewById(R.id.question_item_option_none);
        }
    }

    @Override
    public AnswerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_question, parent, false);
        return new AnswerHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(AnswerHolder holder, int position) {
        Question d = data.get(position);
        holder.txtName.setText((position + 1) + ". " + getNormalStringFromHtml(d.getQuestion()));
        holder.answer.setText("Answer: " + getNormalStringFromHtml(d.getCorrectAnswer()));

        // reset for recycling
        holder.optionNone.setChecked(true);
        holder.answer.setVisibility(View.GONE);

        holder.optionGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = group.findViewById(checkedId);
            String selectedText = selectedRadioButton.getText().toString();

            if (!selectedText.equals(d.getCorrectAnswer())) {
                holder.answer.setVisibility(View.VISIBLE);
                callback.onAnswerSelected(position, false);
            } else {
                holder.answer.setVisibility(View.GONE);
                callback.onAnswerSelected(position, true);
            }
        });
    }

    private String getNormalStringFromHtml(String s) {
        return Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    public void update(List<Question> data) {
        this.data = (data != null) ? data : new ArrayList<>();
        this.notifyDataSetChanged();
    }
}

