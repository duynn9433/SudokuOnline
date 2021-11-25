/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.constant;

/**
 *
 * @author duynn
 */
public class StreamData {

    /* Cách đọc hiểu đống bên dưới?
        => Đống bên dưới được viết comment theo cấu trúc sau:
    
        Tên type // mô tả / dữ liệu gửi đi từ client / dữ liệu trả về từ server
     */
    public enum Type {
        LOCK_SUBMIT,
        SUBMIT,
        CONNECT,
        // auth
        LOGIN, // chức năng đăng nhập / email, password / success hoặc failed
        SIGNUP, // chức năng đăng ký / thông tin đăng ký / success hoặc failed
        LOGOUT, // chức năng đăng xuất / không cần dữ liệu thêm / success hoặc failed

        // main menu
        LIST_ROOM, // lấy danh sách phòng hiện tại / ko cần dữ liệu thêm / dữ liệu danh sách phòng
        CREATE_ROOM, // chức năng tạo phòng / không cần dữ liệu thêm / success hoặc failed
        JOIN_ROOM, // chức năng vào phòng, nếu chưa có đủ người thì chơi, đủ rồi thì xem / id phòng / success hoặc failed
        CHAT_ALL,   //chat trên kênh chat server
        
        //waiting room
        READY,
        START_GAME_FROM_ROOM,
        CHAT_WAITING_ROOM,
        LEAVE_WAITING_ROOM,
        
        // pair match
        FIND_MATCH, // chức năng tìm trận / không cần dữ liệu thêm / success hoặc failed
        CANCEL_FIND_MATCH, // chức năng hủy tìm trận / không cần dữ liệu thêm / success hoặc failed
        REQUEST_PAIR_MATCH, // chức năng hỏi user có đồng ý ghép cặp không / đồng ý hay không / thông tin user sẽ ghép cặp
        RESULT_PAIR_MATCH, // chức năng gửi thông báo đồng ý ghép cặp / _chỉ có server gửi có client loại type này_ / kết quả ghép cặp

        // in game
        DATA_ROOM, // dữ liệu phòng khi vừa vào phòng / id phòng / dữ liệu phòng
        CHAT_ROOM, // chức năng chat phòng / dữ liệu chat / dữ liệu chat (gửi broadcast trong phòng)
        LEAVE_ROOM, // chức năng thoát phòng / không cần dữ liệu thêm / success hoặc failed
        CLOSE_ROOM, // chức năng đóng phòng / chỉ có server gửi type này cho client
        PLAY_AGAIN, // mời chơi lại
        REFUSE_PLAY_AGAIN,//từ chối chơi lại
        ACCEPT_PALY_AGAIN,//đồng ý chơi lại
        
        // profile
        GET_PROFILE, // chức năng xem hồ sơ cá nhân / email user muốn xem thông tin / dữ liệu user
        EDIT_PROFILE, // chức năng chỉnh thông tin cá nhân / thông tin cá nhân mới / success hoặc failed
        CHANGE_PASSWORD, // chức năng đổi mật khẩu / mật khẩu cũ, mật khẩu mới / success hoặc failed
        GET_LIST_RANK,
   
        // specific
        UNKNOW_TYPE, // khi client gửi type không xác định
        EXIT, // chức năng tắt game / không cần dữ liệu thêm / bradcast thoát game
    }

    // https://stackoverflow.com/a/6667365
    public static Type getType(String typeName) {
        Type result = Type.UNKNOW_TYPE;

        try {
            result = Enum.valueOf(StreamData.Type.class, typeName);
        } catch (Exception e) {
            System.err.println("Unknow type: " + e.getMessage());
        }

        return result;
    }

    public static Type getTypeFromData(String data) {
        String typeStr = data.split(";")[0];
        return getType(typeStr);
    }
}
