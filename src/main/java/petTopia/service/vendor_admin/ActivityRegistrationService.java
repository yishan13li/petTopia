package petTopia.service.vendor_admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.vendor_admin.MemberDTO;
import petTopia.model.user.Member;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;

@Service
public class ActivityRegistrationService {
	@Autowired
	private ActivityRegistrationRepository activityRegistrationRepository;

	// 查詢某活動的報名會員資訊（會員ID、名稱、Email）
	public List<MemberDTO> getRegisteredMembersByActivity(Integer vendorActivityId) {
		// 先從資料庫查詢該活動的所有報名記錄
		List<ActivityRegistration> registrations = activityRegistrationRepository
				.findByVendorActivityId(vendorActivityId);

		// 建立一個空的 List 來存放 MemberDTO
		List<MemberDTO> memberDTOList = new ArrayList<>();

		// 用 for 迴圈逐筆處理報名記錄
		for (ActivityRegistration reg : registrations) {
			// 取得報名的會員資訊
			Member member = reg.getMember();

			// 創建 MemberDTO，只存會員 ID、名字、Email
			MemberDTO dto = new MemberDTO(member.getId(), member.getName(), member.getPhone(), member.getBirthdate(),
					member.getGender());

			// 把轉換後的 DTO 加入到列表
			memberDTOList.add(dto);
		}

		// 回傳所有轉換後的會員資訊
		return memberDTOList;
	}

	// 之後用來發送通知
	public void sendNotificationToMembers(Integer vendorActivityId, String message) {
		List<MemberDTO> members = getRegisteredMembersByActivity(vendorActivityId);
		for (MemberDTO member : members) {
			// TODO: 呼叫 NotificationService 來發送通知
		}
	}
}
