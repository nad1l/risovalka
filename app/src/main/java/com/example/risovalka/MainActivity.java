package com.example.risovalka;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    public static final int IDM_RED = 101;
    public static final int IDM_GREEN = 201;
    public static final int IDM_BLACK = 202;
    public static final int IDM_YELLOW = 102;
    public static final int IDM_BLUE = 103;
    public static final int IDM_WHITE = 104;
    public static final int IDM_REST = 203;
    public static final int IDM_PINK = 303;
    public static final int IDM_MIN = 404;
    public static final int IDM_MEDIUM = 403;
    public static final int IDM_MAX = 402;

    public static final int IDM_SAVE = 405;
    private static final int REQUEST_CODE_PERMISSION = 123;

    public Canvas mCanvas;
    public Paint mPaint;
    public Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(30);
        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        checkWritingPermission();
    }



    private MaskFilter mEmboss;
    private MaskFilter mBlur;

    public void colorChanged(int color) {
        mPaint.setColor(color);
    }


    private void checkWritingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
            }
        }
    }

    public class MyView extends View {

        private Path mPath;
        private Paint mBitmapPaint;

        public MyView(Context c) {
            super(c);

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

            mCanvas.drawColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath(mPath, mPaint);


        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);

            mCanvas.drawPath(mPath, mPaint);

            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SubMenu subMenuColor = menu.addSubMenu("Цвет");
        subMenuColor.add(Menu.NONE, IDM_RED, Menu.NONE, "Красный");
        subMenuColor.add(Menu.NONE, IDM_GREEN, Menu.NONE, "Зеленый");
        subMenuColor.add(Menu.NONE, IDM_BLACK, Menu.NONE, "Черный");
        subMenuColor.add(Menu.NONE, IDM_YELLOW, Menu.NONE, "Желтый");
        subMenuColor.add(Menu.NONE, IDM_BLUE, Menu.NONE, "Синий");
        subMenuColor.add(Menu.NONE, IDM_WHITE, Menu.NONE, "Ластик");
        subMenuColor.add(Menu.NONE, IDM_PINK, Menu.NONE, "Фиолетовый");
        menu.add(Menu.NONE, IDM_REST, Menu.NONE, "Очистка");

        SubMenu subMenuWidth = menu.addSubMenu("Толщина");
        subMenuWidth.add(Menu.NONE, IDM_MIN, Menu.NONE, "Тонкая");
        subMenuWidth.add(Menu.NONE, IDM_MEDIUM, Menu.NONE, "Средняя");
        subMenuWidth.add(Menu.NONE, IDM_MAX, Menu.NONE, "Толстая");

        menu.add(Menu.NONE, IDM_SAVE, Menu.NONE, "Сохранить");


        return super.onCreateOptionsMenu(menu);
    }
   void saveImage() {

           String filename = "/storage/emulated/0/DCIM/Camera/";

        try {
            File f = new File(filename + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpeg");
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            if (!f.exists())
                f.createNewFile();

            FileOutputStream out = new FileOutputStream(f);
            Toast.makeText(getApplicationContext(), "SUCSEED", Toast.LENGTH_LONG).show();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case IDM_RED:
                colorChanged(Color.RED);
                break;
            case IDM_GREEN:
                colorChanged(Color.GREEN);
                break;
            case IDM_BLACK:
                colorChanged(Color.BLACK);
                break;
            case IDM_WHITE:
                colorChanged(Color.WHITE);
                break;
            case IDM_YELLOW:
                colorChanged(Color.YELLOW);
                break;
            case IDM_PINK:
                colorChanged(Color.MAGENTA);
                break;
            case IDM_BLUE:
                colorChanged(Color.BLUE);
                break;
            case IDM_REST:
                mCanvas.drawColor(Color.WHITE);
                break;
            case IDM_MIN:
                mPaint.setStrokeWidth(15);
                break;
            case IDM_MEDIUM:
                mPaint.setStrokeWidth(30);
                break;
            case IDM_MAX:
                mPaint.setStrokeWidth(45);
                break;
            case IDM_SAVE:
                saveImage();
                break;
            default:
                return false;

        }
        return true;
    }
}
