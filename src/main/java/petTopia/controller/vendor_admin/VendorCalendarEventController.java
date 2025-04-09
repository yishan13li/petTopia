package petTopia.controller.vendor_admin;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.vendor.CalendarEvent;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;

@Controller
public class VendorCalendarEventController {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private CalendarEventRepository calendarEventRepository;

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@ResponseBody
	@GetMapping("/api/vendor_admin/calendar/{vendorId}")
	public ResponseEntity<?> getCalenderEventsByVendorId(@PathVariable Integer vendorId) {
		Optional<Vendor> vendor = vendorRepository.findById(vendorId);
		if (vendor.isPresent()) {
			List<CalendarEvent> calender = calendarEventRepository.findByVendorId(vendorId);
			return ResponseEntity.ok(calender);

		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ResponseBody
	@PostMapping("/api/vendor_admin/calendar/add")
	public ResponseEntity<?> addCalendarEvent(@RequestParam Integer vendorId, @RequestParam String eventTitle,
			@RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
			@RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
			@RequestParam String color) {

		try {
			Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new Exception("Vendor not found"));

			CalendarEvent calendarEvent = new CalendarEvent();
			calendarEvent.setEventTitle(eventTitle);
			calendarEvent.setStartTime(startTime);
			calendarEvent.setEndTime(endTime);
			calendarEvent.setVendor(vendor); // 这里关联了 vendor_id
			calendarEvent.setCreatedAt(new Date());
			calendarEvent.setUpdatedAt(new Date());
			calendarEvent.setColor(color);
			calendarEventRepository.save(calendarEvent);

			return new ResponseEntity<>(calendarEvent, HttpStatus.CREATED); // 返回成功响应
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 返回失败响应
		}
	}

	@ResponseBody
	@PutMapping("/api/vendor_admin/calendar/update/{id}")
	public ResponseEntity<?> updateCalendar(@PathVariable Integer id, @RequestParam(required = false) String eventTitle,
			@RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
			@RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
			@RequestParam(required = false) String color) {
		Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByEventId(id);
		if (calendarOpt.isPresent()) {
			System.out.println(calendarOpt);
			CalendarEvent calendarEvent = calendarOpt.get(); // 取出对象
			calendarEvent.setEventTitle(eventTitle);
			calendarEvent.setStartTime(startTime);
			calendarEvent.setEndTime(endTime);
			calendarEvent.setColor(color);
			calendarEvent.setUpdatedAt(new Date());

			calendarEventRepository.save(calendarEvent); // 保存更新
			if (calendarEvent.getVendorActivity() != null) {
				Optional<CalendarEvent> calendarOpt2 = calendarEventRepository
						.findByVendorActivityId(calendarOpt.get().getVendorActivity().getId());
				if (calendarOpt2.isPresent()) {
					Optional<VendorActivity> vendorActivityOpt = vendorActivityRepository
							.findById(calendarOpt.get().getVendorActivity().getId());
					VendorActivity vendorActivity = vendorActivityOpt.get();
					vendorActivity.setName(eventTitle);
					vendorActivity.setStartTime(startTime);
					vendorActivity.setEndTime(endTime);
					vendorActivityRepository.save(vendorActivity);
				}
			}
			return new ResponseEntity<>(calendarEvent, HttpStatus.OK); // 返回 200 OK
		}
		if (calendarOpt.isEmpty()) {
			System.out.println("Received id: " + id);

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 找不到时返回 400
	}

	@DeleteMapping("/api/vendor_admin/calendar/delete/{id}")
	public ResponseEntity<?> deleteCalendar(@PathVariable Integer id) {
		Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByEventId(id);
		if (calendarOpt.isPresent()) {
			CalendarEvent calendarEvent = calendarOpt.get(); // 取出对象
			calendarEventRepository.delete(calendarEvent); // 保存更新
			return new ResponseEntity<>(calendarEvent, HttpStatus.OK); // 返回 200 OK
		}
		if (calendarOpt.isEmpty()) {
			System.out.println("Received id: " + id);

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 找不到时返回 400
	}

//	@ResponseBody
//	@GetMapping("/api/vendor_admin/calendar/update/{id}")
//	public ResponseEntity<?> putMethodName(@PathVariable Integer id) {
//		Optional<CalendarEvent> calendarOpt = calendarEventRepository.findById(id);
//		if (calendarOpt.isPresent()) {
//		    System.out.println("Found calendar event: " + calendarOpt.get());
//		    return new ResponseEntity<>(calendarOpt, HttpStatus.OK); // 返回 200 OK
//		    // 执行更新操作
//		} else {
//		    System.out.println("No calendar event found with ID: " + id);
//		    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//		}
//	}

}
