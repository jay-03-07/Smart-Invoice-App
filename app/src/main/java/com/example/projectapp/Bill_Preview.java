package com.example.projectapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Bill_Preview extends AppCompatActivity {

    private TableLayout tableLayoutItems;
    private List<Item> itemList;
    private double totalAmount;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private String shopName ;
    private String shopPhone ;
    private String shopEmail ;
    private String shopAddress;
    private String gstNumber;

    private String Order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_preview);

        // Get the Intent from the previous activity
        Intent intent = getIntent();

        // Get the order ID
        String orderId = OrderIdGenerator.getInstance().getOrderId();

        Order_id = orderId;

        // Get the item list from the Intent
        itemList = (List<Item>) intent.getSerializableExtra("itemList");

        // Get the TableLayout
        tableLayoutItems = findViewById(R.id.tableLayoutItems);

        // Display items in the table
        if (itemList != null) {
            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);
                addRowToTable(i + 1, item.getItemName(), item.getQuantity(), item.getRate(), item.getGstPercentage());
            }
        }

        // Update the Total Amount TextView
        TextView totalAmountTextView = findViewById(R.id.textViewTotalAmount);
        totalAmount = calculateTotalAmount(itemList);
        totalAmountTextView.setText("Total Amount: ₹" + String.format("%.2f", totalAmount));

        // Button to save as PDF
        Button saveAsPDFButton = findViewById(R.id.buttonSaveAsPDF);

        saveAsPDFButton.setOnClickListener(v -> {
            if (checkPermission()) {
                saveAsPDF(itemList, totalAmount);
                saveDetailsToFirebase(itemList, totalAmount, "paid", new OnCompleteSubmit() {
                    @Override
                    public void OnSubmited() {}

                    @Override
                    public void OnFailedData() {}
                });
            } else {
                requestPermission();
            }
        });
        Button unpaidBillsButton = findViewById(R.id.buttonUnpaidBills);
        unpaidBillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsPDF(itemList, totalAmount);
                showConfirmation(itemList, totalAmount, "unpaid");
            }
        });

        Button paidBillsButton = findViewById(R.id.buttonPaidBills);
        paidBillsButton.setOnClickListener(v -> {
            saveAsPDF(itemList, totalAmount);
            showConfirmation(itemList, totalAmount, "paid");
        });


        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Fetch shop details when the activity is opened
        fetchShopDetails();
    }


    private void addRowToTable(int srNo, String itemName, int quantity, double rate, String gstPercentage) {
        // Create a new TableRow
        TableRow newRow = new TableRow(this);

        // Create TextViews for each column and set their properties
        TextView srNoTextView = createTextView(String.valueOf(srNo));
        TextView descriptionTextView = createTextView(itemName);
        TextView qtyTextView = createTextView(String.valueOf(quantity));
        TextView priceTextView = createTextView(String.valueOf(rate));
        TextView gstTextView = createTextView(gstPercentage);
        TextView taxTextView = createTextView(String.valueOf(calculateTax(rate, quantity, gstPercentage)));
        TextView amountTextView = createTextView(String.valueOf(calculateAmount(rate, quantity, calculateTax(rate, quantity, gstPercentage))));

        // Add TextViews to the TableRow
        newRow.addView(srNoTextView);
        newRow.addView(descriptionTextView);
        newRow.addView(qtyTextView);
        newRow.addView(priceTextView);
        newRow.addView(gstTextView);
        newRow.addView(taxTextView);
        newRow.addView(amountTextView);

        // Add the TableRow to the TableLayout
        tableLayoutItems.addView(newRow);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private double calculateTax(double price, int quantity, String gstPercentage) {
        double gst = Double.parseDouble(gstPercentage.replace("%", ""));
        return (price * quantity * gst) / 100;
    }

    private double calculateAmount(double price, int quantity, double tax) {
        return (price * quantity) + tax;
    }

    private double calculateTotalAmount(List<Item> itemList) {
        double totalAmount = 0.0;
        if (itemList != null) {
            for (Item item : itemList) {
                totalAmount += calculateAmount(item.getRate(), item.getQuantity(), calculateTax(item.getRate(), item.getQuantity(), item.getGstPercentage()));
            }
        }
        return totalAmount;
    }

    private void saveAsPDF(List<Item> itemList, double totalAmount) {
        if (isExternalStorageWritable()) {
            try {
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "projectapp");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filePath = dir.getPath() + "/Invoice.pdf";

                String originalPath = filePath;
                File file = new File(originalPath);
                int i = 0;

                while (file.exists()) {
                    originalPath = filePath.replace(".pdf", "_" + i + ".pdf");
                    file = new File(originalPath);
                    i++;
                }
                filePath = originalPath;
                file.createNewFile();

                PdfWriter writer = new PdfWriter(filePath);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                // Add content to the document using a table
                addContentTable(document, itemList, totalAmount);

                document.close();

                Toast.makeText(this, "PDF saved successfully: " + filePath, Toast.LENGTH_SHORT).show();

                // Save the details to Firebase
//                saveDetailsToFirebase(itemList, totalAmount);


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "External storage not available or not writable.", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnCompleteSubmit {
        public void OnSubmited();
        public void OnFailedData();
    }

    private void saveDetailsToFirebase(List<Item> itemList, double totalAmount,String paymentStatus,OnCompleteSubmit onCompleteSubmit) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ordersRef = databaseReference.child("orders").child(userId);
            DatabaseReference customersRef = databaseReference.child("customers").child(userId); // Use user ID as the key

            // Get customer details from the intent
            Intent intent = getIntent();
            String customerName = intent.getStringExtra("customerName");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String address = intent.getStringExtra("address");

            // Save customer details to the "customers" node
            DatabaseReference customerRef = customersRef.child(phoneNumber);
            customerRef.child("address").setValue(address);
            customerRef.child("customerName").setValue(customerName);
            customerRef.child("phoneNumber").setValue(phoneNumber);

            // Save order details to the "orders" node
            DatabaseReference orderRef = ordersRef.child(phoneNumber);

            DatabaseReference itemsRef = orderRef.child(Order_id);

            for (Item item : itemList) {
                DatabaseReference itemRef = itemsRef.push();
                itemRef.child("prod_id").setValue(item.getProdId());
                Log.d("TAG", "saveDetailsToFirebase: "+item.getProdId());
                itemRef.child("gstpercentage").setValue(item.getGstPercentage());
                itemRef.child("quantity").setValue(item.getQuantity());
                itemRef.child("productName").setValue(item.getItemName()); // Assuming getItemName() returns the product name
                itemRef.child("rate").setValue(item.getRate());
//                itemRef.child("totalAmount").setValue(calculateAmount(item.getRate(), item.getQuantity(),calculateAmount(item.getRate(), item.getQuantity(), calculateTax(item.getRate(), item.getQuantity(), item.getGstPercentage())));
                itemRef.child("totalAmount").setValue(totalAmount);
                // Save payment status
                itemRef.child("payment").setValue(paymentStatus);

            }
            onCompleteSubmit.OnSubmited();
            Toast.makeText(this, "Details saved to Firebase", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            onCompleteSubmit.OnFailedData();
        }
    }

    private void showConfirmation(List<Item> itemList,double totalAmount,String paymentStatus ){
        AlertDialog.Builder builder = new AlertDialog.Builder(Bill_Preview.this);
        builder.setTitle("Confirm Navigation")
                .setMessage("Are you sure you want to close this window? Going back to the home screen will finalize the order with the product set as '" + paymentStatus + "'. This action cannot be undone.")
                .setCancelable(false)
                .setPositiveButton("Yes, Close", (dialog, which) -> {
                    // Resetting the order ID before navigating to the home screen
                    saveDetailsToFirebase(itemList, totalAmount, paymentStatus, new OnCompleteSubmit() {
                        @Override
                        public void OnSubmited() {
                            OrderIdGenerator.getInstance().resetOrderId();
                            startActivity(new Intent(Bill_Preview.this, Homepage.class));
                            finish();
                        }

                        @Override
                        public void OnFailedData() {}
                    });
                })
                .setNegativeButton("No, Stay Here", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Function to fetch shop details from the "shops" node
    private void fetchShopDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference shopsRef = databaseReference.child("shops").child(userId);

            // Use ValueEventListener to fetch data from the database
            shopsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Shop details found, you can handle the data here
                         shopName = dataSnapshot.child("shopName").getValue(String.class);
                         shopPhone = dataSnapshot.child("phoneNo").getValue(String.class);
                         shopEmail = dataSnapshot.child("shopEmail").getValue(String.class);
                         shopAddress = dataSnapshot.child("address").getValue(String.class);
                         gstNumber= dataSnapshot.child("gstNo").getValue(String.class);
//                        // Use the shop details as needed
                    } else {
                        // Shop details not found
                        Log.d("ShopDetails", "Shop details not found");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors here
                    Log.e("FirebaseError", "Error fetching shop details: " + databaseError.getMessage());
                }
            });
        } else {
            // User not logged in
            Log.d("ShopDetails", "User not logged in");
        }
    }





    // New method to add content using a table
    private void addContentTable(Document document, List<Item> itemList, double totalAmount) throws IOException {
        // Create a Table for the entire content
        Table contentTable = new Table(new float[]{1});

        // Add header information to the content table
        addHeaderInfo(contentTable);

        // Add items table to the content table
        addItemsTable(contentTable, itemList);

        // Add total amounts table to the content table
        addTotalAmounts(contentTable, totalAmount);

        // Add terms and conditions to the content table
        addTermsAndConditions(contentTable);

        // Add the content table to the document
        document.add(contentTable);
    }

    private void addHeaderInfo(Table contentTable) throws IOException {



//        // Shop details
//        String shopName = "Sandeep Electrical";
//        String shopPhone = "98745632101";
//        String shopEmail = "yourshop@example.com";
//        String shopAddress = "Kalewadi, Pune, MH 411017";
//        String gstNumber = "GSTIN: XXXXXXXXXX"; // Replace with your GST No.

        // Customer details
        Intent intent = getIntent();
        String customerName = intent.getStringExtra("customerName");
        String customerMobile = intent.getStringExtra("phoneNumber");
        String customerAddress = intent.getStringExtra("address");


        // Invoice date
        String invoiceDate = "Invoice Date: " + getCurrentDate(); // Add the method getCurrentDate() to get the current date

        // Unique invoice number
        String invoiceNumber = "Invoice No.: " + generateInvoiceNumber();

        // Create a Table for the header information
        Table headerTable = new Table(new float[]{1, 1});

        // Tax Invoice in the center header
        Paragraph taxInvoice = new Paragraph("Tax Invoice")
                .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER)
                .setFontSize(16)
                .setBold();

        // Shop information in the center header
        Paragraph shopHeader = new Paragraph()
                .add(new Paragraph(shopName).setBold().setFontSize(20).setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER))
                .add(new Paragraph("GSTIN:"+gstNumber).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER));

        // Phone and email in the top right corner
        Paragraph contactInfo = new Paragraph()
                .add(new Paragraph("Phone: " + shopPhone).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT))
                .add(new Paragraph("Email: " + shopEmail).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT))
                .add(new Paragraph("Address:" + shopAddress).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT));

        // GST No., customer details, and invoice information
        Paragraph additionalInfo = new Paragraph()
                .add(new Paragraph("Customer Name: " + customerName).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT))
                .add(new Paragraph("Mobile No.: " + customerMobile).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT))
                .add(new Paragraph("").setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT))
                .add(new Paragraph("Address: " + customerAddress).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT));

        Paragraph invoiceData = new Paragraph().add(new Paragraph(invoiceDate).setFontSize(12).setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT))
                .add(new Paragraph(invoiceNumber).setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT));


        // Add the paragraphs to the header table
        headerTable.addCell(new Cell(1, 2).add(taxInvoice));
        headerTable.addCell((shopHeader));
        headerTable.addCell(contactInfo);
        headerTable.addCell(additionalInfo);
        headerTable.addCell(invoiceData);// Add GST No., customer details, and invoice information

        // Add the header table to the content table
        contentTable.addCell(headerTable);
    }

    // Method to generate a unique invoice number
    private String generateInvoiceNumber() {
        // You can implement your logic to generate a unique invoice number
        // For example, you can use a combination of date and a counter
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        int counter = getInvoiceCounterFromDatabase(); // You need to implement this method
        return formattedDate + String.format("%04d", counter);
    }

    // Example method to get the current invoice counter from a database
    private int getInvoiceCounterFromDatabase() {
        // Implement your logic to retrieve the current invoice counter from a database
        // For example, you can query a database to get the latest counter value
        // This is just an example; you need to replace it with your actual implementation
        // For simplicity, I'm returning a constant value here. In a real scenario, this should be retrieved from the database.
        return 1;
    }

    // Method to get the current date
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    // Modified method to add items table within the content table
    private void addItemsTable(Table contentTable, List<Item> itemList) throws IOException {
        // Create a Table for items
        Table itemsTable = new Table(new float[]{1, 3, 1, 2, 1, 1, 2});
        itemsTable.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

        // Inside addItemsTable method
        Cell srNoHeaderCell = new Cell().add(new Paragraph("Sr No.").setBold());
//        srNoHeaderCell.setPadding(17f); // Set padding

        Cell itemDescriptionHeaderCell = new Cell().add(new Paragraph("Item Description").setBold());
//        itemDescriptionHeaderCell.setPadding(17f); // Set padding
        itemDescriptionHeaderCell.setTextAlignment(TextAlignment.CENTER); // Center text

        Cell qtyHeaderCell = new Cell().add(new Paragraph("Quantity").setBold());
//        qtyHeaderCell.setPadding(17f); // Set padding
        qtyHeaderCell.setTextAlignment(TextAlignment.CENTER); // Center text

        Cell priceHeaderCell = new Cell().add(new Paragraph("Price").setBold());
//        priceHeaderCell.setPadding(17f); // Set padding
        priceHeaderCell.setTextAlignment(TextAlignment.CENTER); // Center text

        Cell gstHeaderCell = new Cell().add(new Paragraph("GST").setBold());
//        gstHeaderCell.setPadding(17f); // Set padding
        gstHeaderCell.setTextAlignment(TextAlignment.CENTER); // Center

        Cell taxHeaderCell = new Cell().add(new Paragraph("Tax").setBold());
//        taxHeaderCell.setPadding(17f); // Set padding
        taxHeaderCell.setTextAlignment(TextAlignment.CENTER); // Center text

        Cell amountHeaderCell = new Cell().add(new Paragraph("Amount").setBold());
//        amountHeaderCell.setPadding(17f); // Set padding
        amountHeaderCell.setTextAlignment(TextAlignment.CENTER); // Center text

        itemsTable.addHeaderCell(srNoHeaderCell);
        itemsTable.addHeaderCell(itemDescriptionHeaderCell);
        itemsTable.addHeaderCell(qtyHeaderCell);
        itemsTable.addHeaderCell(priceHeaderCell);
        itemsTable.addHeaderCell(gstHeaderCell);
        itemsTable.addHeaderCell(taxHeaderCell);
        itemsTable.addHeaderCell(amountHeaderCell);

        // Add items to the table
        for (int i = 1; i < tableLayoutItems.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayoutItems.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView textView = (TextView) row.getChildAt(j);
                Cell dataCell = new Cell().add(new Paragraph(textView.getText().toString()));
//                dataCell.setPadding(17f); // Set padding
                itemsTable.addCell(dataCell);
            }
        }

        // Center the itemsTable within the contentTable
        Cell centeredCell = new Cell().add(itemsTable);
        centeredCell.setTextAlignment(TextAlignment.CENTER);

        // Add items table to the content table
        contentTable.addCell(centeredCell);
    }


    // Modified method to add total amounts table within the content table
    private void addTotalAmounts(Table contentTable, double totalAmount) throws IOException {
        // Create a Table for total amounts
        Table totalAmountTable = new Table(new float[]{4, 1});

        // Calculate total tax
        double totalTax = calculateTotalTax(itemList);

        // Calculate total before tax (sum of amount - tax)
        double totalBeforeTax = totalAmount - totalTax;

        // Calculate total after tax (total before tax + total tax)
        double totalAfterTax = totalBeforeTax + totalTax;

        // Set total amounts
        totalAmountTable.addCell("Total Before Tax:");
        totalAmountTable.addCell(String.format("₹%.2f", totalBeforeTax));

        totalAmountTable.addCell("Tax:");
        totalAmountTable.addCell(String.format("₹%.2f", totalTax));

        // Add "Total After Tax:" text and value as bold
        totalAmountTable.addCell(new Cell().add(new Paragraph("Total After Tax:").setBold())); // Set text as bold
        totalAmountTable.addCell(new Cell().add(new Paragraph(String.format("₹%.2f", totalAfterTax)).setBold())); // Set value as bold

        // Add the total amounts table to the content table
        String amountInWords = NumberToWords.convert(totalAfterTax);

        contentTable.addCell(totalAmountTable);

        // Add Amount in Words to the content table
        contentTable.addCell(new Cell().add(new Paragraph("Amount in Words: " + amountInWords).setBold()));
    }



    // Add a new method to calculate total tax
    private double calculateTotalTax(List<Item> itemList) {
        double totalTax = 0.0;
        if (itemList != null) {
            for (Item item : itemList) {
                totalTax += calculateTax(item.getRate(), item.getQuantity(), item.getGstPercentage());
            }
        }
        return totalTax;
    }

    // Method to add terms and conditions within the content table
    private void addTermsAndConditions(Table contentTable) {
        // Add Terms and Conditions
        contentTable.addCell(new Paragraph("Terms and Conditions:"));
        contentTable.addCell(new Paragraph("Certified that the particulars given above are true and correct form Smart Invoice (stamp)"));
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}