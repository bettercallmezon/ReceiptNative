package com.receipt.maker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private EditText businessNameInput;
    private EditText totalAmountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48, 48, 48, 48);

        businessNameInput = new EditText(this);
        businessNameInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        businessNameInput.setHint("Business Name");
        businessNameInput.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        totalAmountInput = new EditText(this);
        totalAmountInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        totalAmountInput.setHint("Total Amount ($)");
        totalAmountInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);

        Button generateButton = new Button(this);
        generateButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        generateButton.setText("Generate Native PDF");

        root.addView(businessNameInput);
        root.addView(totalAmountInput);
        root.addView(generateButton);

        setContentView(root);

        generateButton.setOnClickListener(v -> generatePdf());
    }

    private void generatePdf() {
        String businessName = businessNameInput.getText().toString().trim();
        String totalAmount = totalAmountInput.getText().toString().trim();

        if (TextUtils.isEmpty(businessName) || TextUtils.isEmpty(totalAmount)) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(32);
        paint.setFakeBoldText(true);
        canvas.drawText("RECEIPT", 40, 60, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(24);
        canvas.drawText("Business: " + businessName, 40, 120, paint);
        canvas.drawText("Total: $" + totalAmount, 40, 170, paint);

        document.finishPage(page);

        File dir = getExternalFilesDir(null);
        if (dir == null) {
            Toast.makeText(this, "Storage not available", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(dir, "NativeReceipt.pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            fos.close();
            Toast.makeText(this, "PDF Saved!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            document.close();
        }
    }
}
