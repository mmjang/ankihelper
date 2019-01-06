package com.mmjang.duckmemo.ui.browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.card.CardHtmlGenerator;
import com.mmjang.duckmemo.data.card.CardType;
import com.mmjang.duckmemo.data.news.NewsEntryPosition;
import com.mmjang.duckmemo.data.note.Note;
import com.mmjang.duckmemo.ui.news.NewsReaderActivity;
import com.mmjang.duckmemo.ui.popup.PopupActivity;
import com.mmjang.duckmemo.util.Constant;
import com.mmjang.duckmemo.util.ViewUtil;

import java.util.List;


/**
 * Created by liao on 2017/4/27.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    private List<Note> mPlansList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        View container;
        TextView headWord;
        TextView content;
        TextView translation;
        TextView link;
        ImageButton btnDelete;
        ImageButton btnEdit;
        ImageButton btnJump;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            container = view;
            headWord = view.findViewById(R.id.note_item_head_word);
            content = view.findViewById(R.id.note_item_content);
            translation = view.findViewById(R.id.note_item_translation);
            link = view.findViewById(R.id.note_item_link);
//            btnDelete = view.findViewWithTag(R.id.note_item_delete);
//            btnEdit = view.findViewById(R.id.note_item_edit);
//            btnJump = view.findViewById(R.id.note_item_jump_source);
//            checkBox = view.findViewById(R.id.note_item_checkbox);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }

    public NoteAdapter(
            Activity activity,
            List<Note> planList) {
        mPlansList = planList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Note note = mPlansList.get(position);
        String hwd = note.getWord();
        String content = note.getSentence();
        holder.translation.setVisibility(View.GONE);
        if(!((NoteBrowserActivity) mActivity).mVisibility){
            hwd = hwd.replaceAll(".", "•");
            content = content.replaceAll("<b>(.*?)</b>",
                    "(<font color=#ffffff>$1</font>)");
            if(note.getTranslation() != null && !note.getTranslation().isEmpty()){
                holder.translation.setText(note.getTranslation());
                holder.translation.setVisibility(View.VISIBLE);
            }
        }
        ViewUtil.setCardHtml(holder.content, content);
        if(note.getWord().isEmpty()){
            holder.headWord.setVisibility(View.GONE);
        }else {
            ViewUtil.setCardHtml(holder.headWord, hwd);
        }
        if(note.getNewsEntryPosition() == null){
            holder.link.setVisibility(View.GONE);
        }else{
            holder.link.setText(note.getNewsEntryPosition().getNewsEntry().getTitle() + " - "
                    + note.getNewsEntryPosition().getNewsEntry().getSource());
        }

        holder.container.setOnCreateContextMenuListener(
                new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
                        MenuItem edit = menu.add(Menu.NONE, 2, 2, "Edit");
                        //MenuItem Source = menu.add(Menu.NONE, 3, 3, "Source");

                        delete.setOnMenuItemClickListener(
                                new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Note note = mPlansList.get(holder.getAdapterPosition());
                                        note.delete();
                                        mPlansList.remove(holder.getAdapterPosition());
                                        NoteAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                                        return true;
                                    }
                                }
                        );

                        edit.setOnMenuItemClickListener(
                                new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Note note = mPlansList.get(holder.getAdapterPosition());
                                        ((NoteBrowserActivity) mActivity).onEditNote(note, holder.getAdapterPosition());
                                        return true;
                                    }
                                }
                        );

                    }
                }
        );

        holder.link.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, NewsReaderActivity.class);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        NewsEntryPosition newsEntryPosition = mPlansList.get(position).getNewsEntryPosition();
                        intent.putExtra(Constant.INTENT_DUCKMEMO_NEWS_ID, newsEntryPosition.getNewsEntry().getId());
                        intent.putExtra(Constant.INTENT_DUCKMEMO_NEWS_POSITION_INDEX, newsEntryPosition.getSentenceIndex());
                        mActivity.startActivity(intent);
                    }
                }
        );

        holder.container.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
                        LayoutInflater inflater = mActivity.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_note_details, null);
                        dialogBuilder.setView(dialogView);

                        final TextView textView = dialogView.findViewById(R.id.dialog_textview_note_content);

                        Note note = mPlansList.get(position);
                        String[] cardhtml = CardHtmlGenerator.getCard(note, CardType.SENTENCE_DEFINITION);
                        ViewUtil.setCardHtml(textView, cardhtml[1]);
//                        //dialogBuilder.setMessage("输入笔记");
//                        dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                mNoteEditedByUser = edt.getText().toString();
//                            }
//                        });
////                        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialog, int whichButton) {
////                                //pass
////                            }
////                        });
                        AlertDialog b = dialogBuilder.create();
                        b.show();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mPlansList.size();
    }
}
