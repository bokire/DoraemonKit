package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.tableview.TableConfig;
import com.didichuxing.doraemonkit.ui.widget.tableview.bean.ArrayTableData;
import com.didichuxing.doraemonkit.ui.widget.tableview.format.FastTextDrawFormat;
import com.didichuxing.doraemonkit.ui.widget.tableview.style.FontStyle;
import com.didichuxing.doraemonkit.ui.widget.tableview.component.SmartTable;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.DBUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBDetailFragment extends BaseFragment {

    private SmartTable table;
    private ListView tableListView;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_db_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle data = getArguments();
        SQLiteDatabase sqLiteDatabase = null;
        List<String> tableNames = new ArrayList<>();
        if (data != null) {
            File mFile = (File) data.getSerializable(BundleKey.FILE_KEY);
            String path = mFile.getPath();
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(path, null);
            tableNames = DBUtil.queryDBtableName(sqLiteDatabase);
        }
        table = findViewById(R.id.table);
        FontStyle fontStyle = new FontStyle(getContext(), 15, ContextCompat.getColor(getContext(), R.color.dk_color_000000));
        TableConfig.getInstance().setVerticalPadding(10).setHorizontalPadding(10);
        TableConfig.getInstance().columnTitleStyle = fontStyle;
        table.setZoom(true, 2f, 0.4f);


        tableListView = findViewById(R.id.lv_table_name);
        tableListView.setAdapter(new DBListAdapter(getContext(), tableNames));
        final List<String> finalStrings = tableNames;
        final SQLiteDatabase finalSqLiteDatabase = sqLiteDatabase;
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }

            @Override
            public void onRightClick() {

            }
        });
        tableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectTableName = finalStrings.get(position);

                String[][] data = DBUtil.queryAll(finalSqLiteDatabase, finalStrings.get(position));
                String[] titleName = DBUtil.queryTableColumnName(finalSqLiteDatabase, selectTableName);
                if (table.getTableData() != null) {
                    table.getTableData().clear();
                }
                table.setTableData(ArrayTableData.create(selectTableName, titleName, data, new FastTextDrawFormat<String>()));
                table.getMatrixHelper().reset();
                tableListView.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected boolean onBackPressed() {
        if (table.getVisibility() == View.VISIBLE) {
            table.setVisibility(View.GONE);
            tableListView.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
        return true;
    }

}