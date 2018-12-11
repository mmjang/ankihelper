package com.mmjang.ankihelper.ui.read;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.book.Book;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.ui.plan.helper.ItemTouchHelperAdapter;
import com.mmjang.ankihelper.ui.plan.helper.ItemTouchHelperViewHolder;

import java.util.List;


/**
 * Created by liao on 2017/4/27.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> implements ItemTouchHelperAdapter{
    private List<Book> mBookList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
        RelativeLayout countainer;
        TextView bookName;
        TextView authorName;
        //LinearLayout layoutEdit;
        LinearLayout layoutDelete;

        public ViewHolder(View view) {
            super(view);
            countainer = view.findViewById(R.id.book_item);
            bookName = (TextView) view.findViewById(R.id.book_name);
            authorName = (TextView) view.findViewById(R.id.author_name);
            //layoutEdit = (LinearLayout) view.findViewById(R.id.layout_edit);
            layoutDelete = (LinearLayout) view.findViewById(R.id.layout_delete);
        }

        @Override
        public void onItemSelected() {
            countainer.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            countainer.setBackgroundColor(0);
        }
    }

    public BooksAdapter(
            Activity activity,
            List<Book> planList) {
        mBookList = planList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Book book = mBookList.get(position);
        holder.bookName.setText(book.getBookName());
        holder.authorName.setText(book.getAuthor());
        holder.layoutDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.confirm_deletion)
                                //.setMessage("Do you really want to whatever?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        int pos = holder.getAdapterPosition();
                                        //mBookList.get(pos).delete();
                                        ExternalDatabase.getInstance().deleteBook(mBookList.get(pos));
                                        mBookList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                }
        );
        holder.countainer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mActivity instanceof BookshelfActivity){
                            int pos = holder.getAdapterPosition();
                            ((BookshelfActivity) mActivity).onOpenBook(mBookList.get(pos));
                        }
                    }
                }
        );

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public void onMoveFinished() {
//        List<OutputPlan> plansInDatabase = DataSupport.findAll(OutputPlan.class);
//        for(int i = 0; i < plansInDatabase.size(); i ++){
//            OutputPlan oldPlan = plansInDatabase.get(i);
//            OutputPlan newPlan = mBookList.get(i);
//            if(oldPlan.getPlanName() == newPlan.getPlanName()){
//                continue;
//            }else{
//                oldPlan.setPlanName(newPlan.getPlanName());
//                oldPlan.setDictionaryKey(newPlan.getDictionaryKey());
//                oldPlan.setOutputDeckId(newPlan.getOutputDeckId());
//                oldPlan.setOutputModelId(newPlan.getOutputModelId());
//                oldPlan.setFieldsMap(newPlan.getFieldsMap());
//                oldPlan.save();
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
