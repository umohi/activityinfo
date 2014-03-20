package org.activityinfo.ui.client.importer;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;


@SuppressWarnings({"NonJREEmulationClassesInClientCode", "AppEngineForbiddenCode"})
public class ExtractDbUnit {

    /**
     * Utility to create a dbunit xml file from a local mysql database
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/activityinfo?zeroDateTimeBehavior=convertToNull", "root", "adminpwd");
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);

        partialDataSet.addTable("userlogin", "select * from userlogin where userid in " +
                "(select owneruserid from userdatabase where databaseid=1100)");

        partialDataSet.addTable("country", "select * from country where countryid=360");
        partialDataSet.addTable("adminlevel", "select * from adminlevel where countryid=360");
        partialDataSet.addTable("adminentity", "select * from adminentity where adminlevelid in " +
                "(select adminlevelid from adminlevel where CountryId=360)");
        partialDataSet.addTable("locationtype", "select * from locationtype where locationtypeid = 1360");
        partialDataSet.addTable("location", "select * from location where locationtypeid = 1360");
        partialDataSet.addTable("locationadminlink", "select * from locationadminlink where locationid in " +
                "(select locationid from location where LocationTypeID=1360)");

        partialDataSet.addTable("userdatabase", "select * from userdatabase where databaseid=1100");
        partialDataSet.addTable("partner", "select * from partner where partnerid " +
                " in (select partnerid from partnerindatabase where databaseid=1100)");

        partialDataSet.addTable("activity", "select * from activity where databaseid=1100 and" +
                " activity.datedeleted is null ");

        partialDataSet.addTable("attribute", "select * from attribute at where " +
                "at.datedeleted is null and at.attributegroupid in " +
                "(select attributegroupid from attributegroupinactivity ag where ag.activityid in " +
                "(select activityid from activity where databaseid=1100 and activity.datedeleted is null)) ");


        FlatXmlDataSet.write(partialDataSet, new FileOutputStream("src/test/resources/dbunit/attrib-import.xml"));

    }
}
