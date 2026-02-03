package pk.gov.pbs.tds.util;

import org.osmdroid.util.GeoPoint;

public class DebugHelper {
    private static final String TAG = "DebugHelper";
    public static GeoPoint getTestGeoPoint(){
        return new GeoPoint(33.679398679965736, 73.03464969598426);
    }

    public static GeoPoint getPakistanCenterPoint(){
        return new GeoPoint(33.678394753792276D, 73.0327016146912D);
    }
    public static String getBlockBoundary(String blockCode){
        return "{ \"type\": \"FeatureCollection\", \"features\": [ { \"type\": \"Feature\", \"properties\": {}, \"geometry\": { \"coordinates\": [ [ [ 68.22578260041158, 23.874972960506128 ], [ 69.16552380836845, 24.218246278402717 ], [ 70.96982692764561, 24.457989242318916 ], [ 70.48116149950778, 25.582017372408217 ], [ 70.03008571968877, 26.258177578052823 ], [ 69.54142029155093, 26.930424135322554 ], [ 70.48116149950778, 27.864862842117333 ], [ 71.87197848728366, 27.964510610825798 ], [ 73.26279547505953, 29.41533936690415 ], [ 74.65361246283538, 31.006974540842336 ], [ 74.57843316619892, 32.000476910440995 ], [ 76.00683980229246, 32.76233537183806 ], [ 77.54801538334135, 32.92024488278851 ], [ 79.31472885429974, 32.50909687758195 ], [ 78.86365307448074, 33.76807874256494 ], [ 79.76580463411989, 34.483759897578125 ], [ 80.21688041393884, 35.4080991946671 ], [ 79.46508744757381, 35.927246780785424 ], [ 78.11186010811565, 35.591713146778886 ], [ 76.382736285475, 35.713889073563536 ], [ 75.78130191238296, 36.35223836827106 ], [ 74.35289527628939, 36.98539598475563 ], [ 72.5485921570122, 36.74480750911391 ], [ 71.15777516923634, 36.018510119835824 ], [ 71.45849235578234, 35.25476726796495 ], [ 70.96982692764561, 34.421767591488305 ], [ 69.91731677473345, 34.01769814207354 ], [ 70.10526501632523, 33.172314786013445 ], [ 69.20311345668722, 32.127898839989015 ], [ 68.41373084200339, 31.77706208122339 ], [ 67.43639998572883, 31.264371634398756 ], [ 66.34630018449897, 30.7165700003424 ], [ 66.3087105361802, 29.840113023781527 ], [ 64.16610058203923, 29.448077769747613 ], [ 60.63267364012245, 29.970451442220167 ], [ 61.873132034625314, 28.527438630141063 ], [ 62.81287324258108, 28.19666421021938 ], [ 63.1887697257647, 27.198211316253122 ], [ 62.6249250009904, 26.661999646234804 ], [ 61.49723555144283, 25.7852721374491 ], [ 61.49723555144283, 25.174472511232423 ], [ 63.11359042912818, 25.242492027262614 ], [ 65.21861073495032, 25.378416834646117 ], [ 66.42147948113544, 25.480260201412975 ], [ 67.24845174413701, 24.62895558739409 ], [ 68.22578260041158, 23.874972960506128 ] ] ], \"type\": \"Polygon\" } } ]}";
    }

//    public static void createDummyBlock(FormRepository repository){
//        Long count = repository.getAssignmentDao().getAssignmentsCount();
//        if (count != null && count > 0)
//            return;
//        repository.executeDatabaseOperation(new IDatabaseOperation<ArrayList<Long>>() {
//            @Override
//            public ArrayList<Long> execute(ModelBasedDatabaseHelper db) {
//                ArrayList<Long> result = new ArrayList<>();
//                Establishment obj = new Establishment();
//
//                obj.PCode = "0000000000";
//                obj.EBCode = "1111111111";
//                obj.Assigner = "5555555555555";
//                obj.DBegin = "01/03/2025";
//                obj.DEnd = "30/4/2025";
//                obj.DAssigned = "08/02/2025";
//                obj.Assignee = "6666666666666";
//                obj.status = 1;
//                obj.BoundaryGeoJson = getBlockBoundary(obj.EBCode);
//                obj.province = "Punjab";
//                obj.district = "Lahore";
//                obj.tehsil = "Lahore";
//
//                result.add(repository.getDatabase().insert(obj));
//
//                obj.PCode = "9999999999";
//                obj.EBCode = "2222222222";
//                obj.DBegin = "01/06/2025";
//                obj.DEnd = "31/7/2025";
//                obj.DAssigned = "08/02/2025";
//                obj.BoundaryGeoJson = getBlockBoundary(obj.EBCode);
//
//                result.add(repository.getDatabase().insert(obj));
//                return result;
//            }
//
//            @Override
//            public void postExecute(ArrayList<Long> result) {
//                if (!result.isEmpty())
//                    Log.d(TAG, "postExecute: Insertion Completed! " + result.size() + " Entries added");
//                else
//                    Log.d(TAG, "postExecute: Insertion failed!");
//
//            }
//        });
//    }
}
