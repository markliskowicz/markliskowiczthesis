package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import FBO.PostFBO;
import FBO.SMPost;
import FBO.StoredSMPost;



@Controller
public class AppController {
	
	public AppController() {
		//fileDao = new FileDao();
		//userDao = new UserDao();
		//postDao = new SMPostDao();
		
		facebookPoster = new FacebookPoster(fileDao);
		instagramPoster = new InstagramPoster(fileDao);
		//twitterPin = new TwitterPin();
	}

	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private FileDao fileDao;
	
	@Autowired
	private StorageService storageService;
	
	private Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	
	@Autowired	
	private UserDao userDao;
	
	@Autowired
	private SMPostDao postDao;
	
	private String ownerName ;//= authentication.getName();
	private int owner = -1;//userDao.getID(ownerName);
	
	private StoredSMPost currentPost;
	
	//@Autowired
	private TwitterPoster twitterPoster;
	
	//@Autowired 
	private FacebookPoster facebookPoster;
	
	//@Autowired
	private InstagramPoster instagramPoster;
	
	//@Autowired 
	private TwitterPin twitterPin;

	@GetMapping(value = "/")
	public String postHomeFromRoot(Principal principal) {
		getUserIdOnStart(principal);
		System.out.println("got to postHome()");
		return "home";
	}

	private void getUserIdOnStart(Principal principal) {
		if(principal != null && owner == -1) {
			ownerName = principal.getName();
			System.out.println(ownerName);
			owner = userDao.getID(ownerName);
		}
	}
	
	@GetMapping(value = "/home")
	public String postHome(Principal principal) {
		//System.out.println("got to postHome()");
		getUserIdOnStart(principal);
		return "home";
	}
	
	@GetMapping(value = "/posts")
	public String handleSearch(@RequestParam(name = "id", required=false, defaultValue="0") String id, Model model) {
		if(id.equals("0")) {
			List<String> postIDList = postDao.getPostIDs(owner);
			model.addAttribute("posts", postIDList);
			return "posts";
		} else {
			//find the product with the matching id
			StoredSMPost post = postDao.getPostbyID(Integer.parseInt(id));
			currentPost = post;
			if(post == null) {
				//if nothing is found, return everything
				List<String> postIDList = postDao.getPostIDs(owner);
				model.addAttribute("posts", postIDList);
				return "posts";
			}
			//return the product
			model.addAttribute("post", post);
			return "redirect:/savedPost";
		}		
	}
	
	@GetMapping(value = "/savedPost")
	public String showSavedPost(StoredSMPost post, Model model) throws IOException {
		model.addAttribute("post", currentPost);
		return "savedPost";
	}
	
	@PostMapping(value = "/savedPost")
	public String restoreSavedPost(@Valid @ModelAttribute("post")StoredSMPost smpost, BindingResult result, Model model) throws IOException {
		String sites = smpost.getWebsite();
		if(sites.charAt(0) == '1') {
    		//twitterPoster = new TwitterPoster();
    		
    		twitterPoster.restore(smpost);
    	}
    	if(sites.charAt(1) == '1') {
    		//InstagramPoster instagramPoster = new InstagramPoster();
    		instagramPoster.restore(smpost);
    	}
    	if(sites.charAt(2) == '1') {
    		//FacebookPoster facebookPoster = new FacebookPoster();
    		facebookPoster.restore(smpost);
    	}
		return "savedPost";
	}
	
	@GetMapping(value = "/createAccount")
	public String showCreateAccount(Account account, Model model) throws IOException {
		//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAgot to showCreateAccount()");
		model.addAttribute("account", new Account());
		return "createAccount";
	}

	@PostMapping(value = "/createAccount")
	public String createAccount(@Valid @ModelAttribute("account") Account account, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "createAccount";
		} else {
			model.addAttribute(account);
			if(!account.getPassword().equals(account.getConfirmPassword())){
				return "createAccount";
			}
			account.setRole("user");
			account.setConfirmPassword(encodePassword(account.getConfirmPassword()));
			account.setPassword(encodePassword(account.getPassword()));
			accountDao.createAccount(account);
			return "home";
		}
	}

	private String encodePassword(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}

	@RequestMapping(value = "/showLogout", method = RequestMethod.GET)
	public String showLogoutForm() {
		return "logout";
	}
	
	/*
	 * @Autowired public AppController(StorageService storageService) {
	 * this.storageService = storageService; }
	 */

    @GetMapping("/viewFiles")
    public String listUploadedFiles(Model model) throws IOException {
    	System.out.println("got to listUploadedFiles()");
		/*
		 * model.addAttribute("files", storageService.loadAll().map( path ->
		 * MvcUriComponentsBuilder.fromMethodName(AppController.class, "serveFile",
		 * path.getFileName().toString()).build().toString())
		 * .collect(Collectors.toList()));
		 */
    	//FileDao fileDao = new FileDao();
    	ArrayList<String> listOfFiles = fileDao.getFileNames(owner);
    	model.addAttribute("files", listOfFiles);
        return "UploadFile";
       
    }

//    @GetMapping("/files/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//
//        Resource file = storageService.loadAsResource(filename);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }

    @PostMapping("/viewFiles")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
    	System.out.println("got to handleFileUpload()");
    	//FileDao fileDao = new FileDao();
    	//storageService.store(file);
    	CloudinaryUploader uploader = new CloudinaryUploader(fileDao);
    	uploader.saveFile(file, owner, fileDao); 
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/viewFiles";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/createPost")
    public String handlePostUpload(HttpServletRequest servletRequest, 
            @ModelAttribute PostFBO post,
            Model model)  {
    	// need seperate post fbo
    	boolean instagramPostResult = true;
    	boolean facebookPostResult = true;
    	boolean twitterPostResult = true;
    	List<MultipartFile> files = post.getPhotos();
        List<String> fileNames = new ArrayList<String>();
        SMPost smpost = new SMPost();
        smpost.setStoredPhotoURL(url);
        CloudinaryUploader uploader = new CloudinaryUploader(fileDao);
        if (null != files && files.size() > 0) 
        {
            for (int i = 0; i < files.size(); i++) {
 
                //File imageFile = new File(servletRequest.getServletContext().getRealPath("/image"), fileName);
                uploader.saveFile(files.get(i), owner, fileDao);
                smpost.addFile(files.get(i));
//                try
//                {
//                    multipartFile.transferTo(imageFile);
//                } catch (IOException e) 
//                {
//                    e.printStackTrace();
//                }
            }
        }
        model.addAttribute("post", post);
        smpost.setBody(post.getBody());
        smpost.setOwner(owner);
        String website = "";
    	if(post.isPostToTwitter()) {
    		//twitterPoster = new TwitterPoster();
    		website = website + "1";
    		twitterPoster.post(smpost);
    	} else {
    		website = website + "0";
    	}
    	if(post.isPostToInstagram()) {
    		//InstagramPoster instagramPoster = new InstagramPoster();
    		website = website + "1";
    		instagramPostResult = instagramPoster.post(smpost);
    	} else {
    		website = website + "0";
    	}
    	if(post.isPostToFacebook()) {
    		//FacebookPoster facebookPoster = new FacebookPoster();
    		website = website + "1";
    		facebookPostResult = facebookPoster.post(smpost);
    	} else {
    		website = website + "0";
    	}
    	if(instagramPostResult && facebookPostResult && twitterPostResult) {
    		//SMPostDao postDao = new SMPostDao();
    		postDao.addPost(smpost);
    		
    	}
    	
    	return "createPost";
    }
    
    String url = "";
    
    @GetMapping(value = "/createPost")
	public String showPostUpload(@RequestParam(name = "url", required=false, defaultValue="") String url, SMPost post, Model model) throws IOException {
		model.addAttribute("post", new SMPost());
		this.url = url;
		return "createPost";
	}
    
//    @GetMapping(value = "/twitterPin")
//	public String showTwitterPinPage(TwitterPin twitterPin, Model model) throws IOException {
//    	
//		model.addAttribute("pin", twitterPin);
//		return "twitterPin";
//	}
    
    @PostMapping(value = "/twitterPin")
	public String getTwitterPinPage(@Valid @ModelAttribute("pin") TwitterPin twitterPin, BindingResult result, Model model) {
		this.twitterPin = twitterPin;
    	twitterPoster.getAccessTokenFromPIN(twitterPin.getPin());
		return "createPost";
	}
    
    @PostMapping(value ="/restoreAll")
    public String restoreAllPosts(Model model) {
    	ArrayList<StoredSMPost> posts = postDao.getAll(owner);
    	for(int i = 0; i < posts.size(); i++) {
    		StoredSMPost post = posts.get(i);
    		if(post.isPostToFacebook()) {
    			facebookPoster.restore(post);
    		} 
    		if(post.isPostToInstagram()) {
    			instagramPoster.restore(post);
    		}
    		if(post.isPostToTwitter()) {
    			twitterPoster.restore(post);
    		}
    	}
    	return "home";
    }
     
    @GetMapping(value = "/twitterLogin")
	public String showTwitterLoginPage(TwitterPin twitterPin, Model model) throws IOException {
    	twitterPoster = new TwitterPoster(fileDao);
    	twitterPin = new TwitterPin();
		twitterPin.setURL(twitterPoster.getAuthenticationURL()); 
		model.addAttribute("twitterPin", twitterPin);
		return "twitterPin";
	}
    
    @GetMapping(value = "/token")
	public String getIGAccessToken(@RequestParam(name = "access_token", required=false, defaultValue="0") String token, Model model) {
    	if(token.equals("0")) {
    		return "home";
    	} 
    	instagramPoster.setAccessToken(token);
    	System.out.println(token);
    	return "confirmation";
    }
    
    @GetMapping("/selectFile")
    public String selectUploadedFiles(Model model) throws IOException {
    	
		/*
		 * model.addAttribute("files", storageService.loadAll().map( path ->
		 * MvcUriComponentsBuilder.fromMethodName(AppController.class, "serveFile",
		 * path.getFileName().toString()).build().toString())
		 * .collect(Collectors.toList()));
		 */
    	//FileDao fileDao = new FileDao();
    	ArrayList<String> listOfFiles = fileDao.getFileNames(owner);
    	model.addAttribute("files", listOfFiles);
        return "selectFile";
       
    }
    
    
    
    
}