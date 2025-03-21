package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.user.MemberBean;
import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.util.ImageConverter;

@Service
public class ActivityRegistrationUserService {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private ActivityRegistrationRepository activityRegistrationRepository;

	@Autowired
	private ActivityPeopleNumberRepository activityPeopleNumberRepository;

	/* 報名與取消活動 */
	public boolean toggleRegistration(Integer memberId, Integer activityId) {
		MemberBean member = memberRepository.findById(memberId).orElse(null);
		VendorActivity activity = vendorActivityRepository.findById(activityId).orElse(null);
		ActivityRegistration registration = activityRegistrationRepository.findByMemberAndVendorActivity(member,
				activity);

		if (registration == null) {
			ActivityRegistration newRegistration = new ActivityRegistration();
			newRegistration.setMember(member);
			newRegistration.setVendorActivity(activity);
			activityRegistrationRepository.save(newRegistration);

			ActivityPeopleNumber peopleNumber = activityPeopleNumberRepository.findByVendorActivityId(activityId);
			Integer currentParticipants = peopleNumber.getCurrentParticipants();
			currentParticipants += 1;
			peopleNumber.setCurrentParticipants(currentParticipants);
			activityPeopleNumberRepository.save(peopleNumber);

			return true;
		} else {
			Integer registrationId = registration.getId();
			activityRegistrationRepository.deleteById(registrationId);

			ActivityPeopleNumber peopleNumber = activityPeopleNumberRepository.findByVendorActivityId(activityId);
			Integer currentParticipants = peopleNumber.getCurrentParticipants();
			currentParticipants -= 1;
			peopleNumber.setCurrentParticipants(currentParticipants);
			activityPeopleNumberRepository.save(peopleNumber);

			return false;
		}
	}

	/* 獲取報名未核准清單 */
	public List<ActivityRegistration> getActivityPendingList(Integer activityId) {
		List<ActivityRegistration> pendingList = activityRegistrationRepository
				.findByVendorActivityIdAndStatus(activityId, "pending");

		for (ActivityRegistration registration : pendingList) {
			byte[] photoByte = registration.getMember().getProfilePhoto();
			if (photoByte != null) {
				String mimeType = ImageConverter.getMimeType(photoByte);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(photoByte);
				registration.getMember().setProfilePhotoBase64(base64);
			}
		}

		return pendingList;
	}

	/* 獲取報名核准清單 */
	public List<ActivityRegistration> getActivityConfirmedList(Integer activityId) {
		List<ActivityRegistration> confirmedList = activityRegistrationRepository
				.findByVendorActivityIdAndStatus(activityId, "confirmed");

		for (ActivityRegistration registration : confirmedList) {
			byte[] photoByte = registration.getMember().getProfilePhoto();
			if (photoByte != null) {
				String mimeType = ImageConverter.getMimeType(photoByte);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(photoByte);
				registration.getMember().setProfilePhotoBase64(base64);
			}
		}

		return confirmedList;
	}

	/* 取得活動當前與最大人數 */
	public ActivityPeopleNumber getPeopleNumber(Integer activityId) {
		ActivityPeopleNumber peopleNumber = activityPeopleNumberRepository.findById(activityId).orElse(null);
		return peopleNumber;
	}

}
