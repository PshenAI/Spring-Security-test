package ua.kiev.prog;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collection;
import java.util.List;

@Controller
public class MyController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public MyController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "fail", required = false) String fail){
        User user = getCurrentUser();

        String login = user.getUsername();
        CustomUser dbUser = userService.findByLogin(login);

        model.addAttribute("login", login);
        model.addAttribute("roles", user.getAuthorities());
        model.addAttribute("admin", isAdmin(user));
        model.addAttribute("email", dbUser.getEmail());
        model.addAttribute("phone", dbUser.getPhone());
        model.addAttribute("address", dbUser.getAddress());
        model.addAttribute("photo", dbUser.getId());
        if(fail != null && !fail.equals("")){
            model.addAttribute("fail", fail);
        }

        return "index";
    }
    @GetMapping(value = "/img/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getPhoto(@PathVariable Long id) throws IOException {
        String imgFolder = "C:\\Users\\Velvet X\\Documents\\Java Studies\\Java Pro\\SecTest\\img";
        File[] imgs = new File(imgFolder).listFiles();
        File res = new File(imgFolder + "\\dummy.jpg");
        for(File img : imgs){
            String fileName = img.getName();
            if(fileName.startsWith(id.toString())){
                res = img;
            }
        }
        InputStream is = new FileInputStream(res);
        return IOUtils.toByteArray(is);
    }

    @PostMapping( "/uploadFile/{id}")
    public String submit(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws IOException {
        File uploadedFile = oldPhotoRetriever(id);
        String fileExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().length() - 3);

        if(fileExtension.equals("jpg") || fileExtension.equals("png")){
            byte[] res = file.getBytes();
            writeByte(res, uploadedFile);
            return "redirect:/";
        } else {
            return "redirect:/?fail=File must be either '.jpg' or '.png'";
        }
//        String img = "C:\\Users\\Velvet X\\Documents\\Java Studies\\Java Pro\\SecTest\\img\\"
//                + fileName;
    }

    @PostMapping(value = "/update")
    public String update(@RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone) {
        phone = phone.substring(0, phone.length()-1);
        if( !email.equals("") && !email.matches(".*[a-z@.].*")){
            System.out.println("EMAIL WORKING");
            return "redirect:/?fail=Check your email!";
        } else if (!phone.equals(",") && !phone.matches(".*[\\d]")){
            System.out.println("PHONE WORKING" + phone);
            return "redirect:/?fail=Check your phone number!";
        }

        User user = getCurrentUser();

        String login = user.getUsername();
        userService.updateUser(login, email, phone);

        return "redirect:/";
    }

    @PostMapping(value = "/newuser")
    public String update(@RequestParam String login,
                         @RequestParam String password,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String address,
                         Model model) {
        String passHash = passwordEncoder.encode(password);

        if ( ! userService.addUser(login, passHash, UserRole.USER, email, phone, address)) {
            model.addAttribute("exists", true);
            model.addAttribute("login", login);
            return "register";
        }

        return "redirect:/";
    }

    @PostMapping(value = "/delete")
    public String delete(@RequestParam(name = "toDelete[]", required = false) List<Long> ids,
                         Model model) {
        userService.deleteUsers(ids);
        model.addAttribute("users", userService.getAllUsers());

        return "admin";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // !!!
    public String admin(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    @GetMapping("/unauthorized")
    public String unauthorized(Model model){
        User user = getCurrentUser();
        model.addAttribute("login", user.getUsername());
        return "unauthorized";
    }

    // ----

    private User getCurrentUser() {
        return (User)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private boolean isAdmin(User user) {
        Collection<GrantedAuthority> roles = user.getAuthorities();

        for (GrantedAuthority auth : roles) {
            if ("ROLE_ADMIN".equals(auth.getAuthority()))
                return true;
        }

        return false;
    }

    private File oldPhotoRetriever(Long id){
        String imgFolder = "C:\\Users\\Velvet X\\Documents\\Java Studies\\Java Pro\\SecTest\\img\\";
        File[] imgs = new File(imgFolder).listFiles();
        for(File img : imgs){
            String fileName = img.getName();
            if(fileName.startsWith(id.toString())){
                return img;
            }
        }
        return null;
    }

    private void writeByte(byte[] bytes, File file){
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}
