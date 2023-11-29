package edu.pnu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.BigTrash;
import edu.pnu.domain.Board;
import edu.pnu.domain.Member;
import edu.pnu.domain.dto.BigTrashRequestDto;
import edu.pnu.persistence.BigTrashRepository;
import edu.pnu.persistence.BoardRepository;
import edu.pnu.persistence.CommentRepository;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.service.BoardService;
import edu.pnu.service.CommentService;

@RestController
@RequestMapping("/api/user")
public class BoardController {
	@Autowired
	private BoardService boardService;
	@Autowired
	private BoardRepository boardRepo;
	@Autowired
	private CommentRepository commRepo;
	@Autowired
	private CommentService CommService;
	@Autowired
	private MemberRepository memRepo;
	@Autowired
	private BigTrashRepository bigRepo;


	@PostMapping("/nowWrite")
	public ResponseEntity<?> postBoardList(@RequestBody BigTrashRequestDto bigTrashRequestDto, @AuthenticationPrincipal User user) {
		List<BigTrash> bigTrash = bigRepo.findBySidoAndCateAndNameAndSize(bigTrashRequestDto.getSido(), bigTrashRequestDto.getCate(), bigTrashRequestDto.getName(), bigTrashRequestDto.getSize());
		
		String name = user.getUsername();
		
		Member member = memRepo.findById(name).get();
		
		boardRepo.save(Board.builder()
				.member(member)
				.title(bigTrashRequestDto.getTitle())
				.content(bigTrashRequestDto.getContent())
				.image(bigTrashRequestDto.getImage())
				.tag(bigTrashRequestDto.getTag())
				.bigTrash(bigTrash.get(0))
				.build()); 
		return ResponseEntity.ok().build();
	}
  
		/*ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		
		Board B = new Board();
		JsonNode jsonNode;
		try {
			//board -> requestBody 수정함, 위 매개변수도 Board board 에서 수정 (틀릴수도 있음 )
			jsonNode = objectMapper.readTree(board);
			String username = jsonNode.get("username").asText();
			String title = jsonNode.get("title").asText();
			String content = jsonNode.get("content").asText();
			String tag = jsonNode.get("tag").asText();
			Integer bigId = jsonNode.get("bigId").asInt();

			Optional<Member> opM = memRepo.findByUsername(username);
			Optional<BigTrash> bts = bigRepo.findById(bigId);
			Optional<String> listob = bigRepo.findNameAndCateByBigId(bigId);
			String[] arr = listob.get().split(",");
			String name = arr[0];
			String cate = arr[1];
			
			if (opM.isPresent() && bts.isPresent()) {
				B.setUsername(opM.get());
				B.setTitle(title);
				B.setContent(content);
				B.setImage(image);
				B.setTag(tag);
				B.setBigId(bts.get());
				
				map.put("name", name);
				map.put("cate", cate);
				map.put("bigId", B.getBigId());
				map.put("postId", B.getPostId());
				map.put("username", B.getUsername());
				map.put("title", B.getTitle());
				map.put("content", B.getContent());
				map.put("image", B.getImage());
				map.put("count", B.getCount());
				map.put("tag", B.getTag());
				map.put("createDate", B.getCreateDate());
				boardRepo.save(B);
	
				return ResponseEntity.ok().body(map);
			} else {
				System.out.println("일치하는 유저가 존재하지 않음");
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		*/
		
	
	//게시글 목록 출력 [ o ]
	@GetMapping("/nowList")
	public ResponseEntity<?> getBoardList(@RequestParam(required = false) String username) {
		Page<Board> boardList = boardService.getBoardList(username);
		return ResponseEntity.ok(boardList);
	}

	//check
	// 댓글 받아오기 /api/user/nowComment?postId=? [ o ]
	@GetMapping("/nowComment")
	public ResponseEntity<?> getComment(Board board){
		
		return ResponseEntity.ok().body(CommService.getCommentByBoardSeq(board));
	}
	
	// 게시글 상세정보 받아오기 /api/user/nowBoard?postId=? [ o ]
	@GetMapping("/nowBoard")
	public ResponseEntity<?> getPostDetail(Integer postId, Integer bigId) {
	
		return ResponseEntity.ok().body(boardService.getPostDetail(postId));
	}
	
	//게시글을 삭제하면 댓글도 같이 삭제 /api/user/delBoard?postId=? [ o ]
	@DeleteMapping("/delBoard")
	public ResponseEntity<?> delBoard(@RequestParam Integer postId){
		try {
		boardService.deleteBoard(postId);
		CommService.deleteComm(postId);
		return ResponseEntity.ok("게시글, 댓글 삭제 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 오류");
		}
	}
	
	//댓글 삭제 /api/user/delComment?commentId=? [ o ]
	@DeleteMapping("/delComment")
	public ResponseEntity<?> delComm(@RequestParam Integer commentId, Board board){
		CommService.deleteComm(commentId);
		return ResponseEntity.ok().body(CommService.getCommentByBoardSeq(board));
	}
}
