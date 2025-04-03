package petTopia.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private VendorActivityRepository vendorActivityRepository;

    @Override
    public long getTotalOrders() {
        return orderRepository.countTotalOrders();
    }

    @Override
    public long getTotalMembers() {
        return memberRepository.countTotalMembers();
    }

    @Override
    public long getTotalProducts() {
        return productRepository.countTotalProducts();
    }

    @Override
    public long getTotalVendors() {
        return vendorRepository.countTotalVendors();
    }

    @Override
    public long getTotalActivities() {
        return vendorActivityRepository.countTotalActivities();
    }

    @Override
    public long getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }
} 