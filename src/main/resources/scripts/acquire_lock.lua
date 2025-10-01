-- KEYS[1]: 잠금을 설정할 키 (e.g., "seat_lock:1")
-- ARGV[1]: 잠금을 소유한 클라이언트의 고유 ID
-- ARGV[2]: 잠금 유효 시간 (초 단위)

-- 키가 존재하지 않을 때만(NX) 값을 설정하고, 유효시간(EX)을 부여한다.
local result = redis.call('SET', KEYS[1], ARGV[1], 'NX', 'EX', ARGV[2])

if result then
  return 1 -- 잠금 획득 성공
else
  return 0 -- 잠금 획득 실패
end